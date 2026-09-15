import { clerkClient, verifyToken } from "@clerk/nextjs/server";
import type { NextRequest } from "next/server";

/**
 * A verified mobile user, derived from a Clerk session token sent as an
 * `Authorization: Bearer <token>` header by the Android app.
 */
export type MobileUser = {
  id: string;
  email: string | null;
  username: string | null;
  image: string | null;
};

/**
 * Verify the mobile Bearer token against Clerk and return the user, or null
 * when the header is missing or the token fails verification.
 */
export async function getMobileUser(req: NextRequest): Promise<MobileUser | null> {
  const header = req.headers.get("authorization") ?? "";
  const token = header.startsWith("Bearer ") ? header.slice(7).trim() : "";
  if (!token) return null;

  let payload;
  try {
    payload = await verifyToken(token, {
      secretKey: process.env.CLERK_SECRET_KEY,
    });
  } catch {
    return null;
  }

  // Org-scoped tokens carry an "orgId:role" subject. The mobile app only uses
  // plain user session tokens, so reject org-scoped ones.
  const userId = payload.sub;
  if (!userId || userId.includes(":")) return null;

  try {
    const client = await clerkClient();
    const user = await client.users.getUser(userId);
    const email =
      user.primaryEmailAddress?.emailAddress ??
      user.emailAddresses[0]?.emailAddress ??
      null;
    return {
      id: user.id,
      email,
      username: user.username ?? null,
      image: user.imageUrl ?? null,
    };
  } catch {
    // Token is valid but the profile fetch failed; still authenticate by id.
    return { id: userId, email: null, username: null, image: null };
  }
}
