rootProject.name = "gitforge"

include("app", "common")

listOf(
    "identity", "org", "repository", "gitstore", "gittransport", "browse",
    "pullrequest", "issue", "access", "activity", "search"
).forEach { include("modules:$it") }