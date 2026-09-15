import { type NextRequest, NextResponse } from "next/server";

import { getMobileUser } from "@/lib/mobile-auth";

// GET /api/v1/me — authenticated by the Clerk session token the Android app
// sends as `Authorization: Bearer <token>`.
export const GET = async (req: NextRequest) => {
  const user = await getMobileUser(req);
  if (!user) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }
  return NextResponse.json(user);
};
