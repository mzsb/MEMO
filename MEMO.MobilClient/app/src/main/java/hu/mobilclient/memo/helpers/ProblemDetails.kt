package hu.mobilclient.memo.helpers

object ProblemDetails {
    fun getDetail(problem: String?): String?{
        return problem?.split("detail")?.get(1)?.split('"')?.get(2)
    }

    fun getTitle(problem: String?): String?{
        return problem?.split("title")?.get(1)?.split('"')?.get(2)
    }

    fun getStatus(problem: String?): String?{
        return problem?.split("status")?.get(1)?.split('"')?.get(2)
    }
}