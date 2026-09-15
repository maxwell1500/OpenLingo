import { type NextRequest, NextResponse } from "next/server";
import { and, eq } from "drizzle-orm";

import { getMobileUser } from "@/lib/mobile-auth";
import db from "@/db/drizzle";
import { challengeProgress, userProgress } from "@/db/schema";

interface SyncRequestBody {
  activeCourseId?: number;
  points?: number;
  hearts?: number;
  completedChallengeIds?: number[];
}

export const POST = async (req: NextRequest) => {
  // 1. Authenticate mobile Clerk user via Bearer token
  const user = await getMobileUser(req);
  if (!user) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  try {
    const body = (await req.json()) as SyncRequestBody;
    const clientPoints = Math.max(0, body.points ?? 0);
    const clientHearts = Math.max(0, Math.min(5, body.hearts ?? 5));
    const clientCourseId = body.activeCourseId ?? 1;
    const clientCompletedChallengeIds = body.completedChallengeIds ?? [];

    let mergedPoints = clientPoints;
    let mergedHearts = clientHearts;
    let mergedCourseId = clientCourseId;

    // Check if Neon DB connection is configured
    if (process.env.DATABASE_URL) {
      // 2. Fetch existing user progress from Postgres
      const existing = await db.query.userProgress.findFirst({
        where: eq(userProgress.userId, user.id),
      });

      if (existing) {
        mergedPoints = Math.max(existing.points, clientPoints);
        mergedHearts = clientHearts; // use latest client hearts
        mergedCourseId = clientCourseId || existing.activeCourseId || 1;

        await db
          .update(userProgress)
          .set({
            points: mergedPoints,
            hearts: mergedHearts,
            activeCourseId: mergedCourseId,
            userName: user.username || user.email?.split("@")[0] || "Learner",
            userImageSrc: user.image || "/mascot.svg",
          })
          .where(eq(userProgress.userId, user.id));
      } else {
        await db.insert(userProgress).values({
          userId: user.id,
          points: mergedPoints,
          hearts: mergedHearts,
          activeCourseId: mergedCourseId,
          userName: user.username || user.email?.split("@")[0] || "Learner",
          userImageSrc: user.image || "/mascot.svg",
        });
      }

      // 3. Batch insert completed challenge progress
      for (const challengeId of clientCompletedChallengeIds) {
        const existingProgress = await db.query.challengeProgress.findFirst({
          where: and(
            eq(challengeProgress.userId, user.id),
            eq(challengeProgress.challengeId, challengeId)
          ),
        });

        if (!existingProgress) {
          await db.insert(challengeProgress).values({
            userId: user.id,
            challengeId: challengeId,
            completed: true,
          });
        }
      }
    }

    return NextResponse.json({
      synced: true,
      user: {
        id: user.id,
        email: user.email,
        username: user.username,
        points: mergedPoints,
        hearts: mergedHearts,
        activeCourseId: mergedCourseId,
      },
    });
  } catch (error) {
    console.error("Cloud sync error:", error);
    return NextResponse.json(
      { error: "Internal Server Error during cloud synchronization" },
      { status: 500 }
    );
  }
};
