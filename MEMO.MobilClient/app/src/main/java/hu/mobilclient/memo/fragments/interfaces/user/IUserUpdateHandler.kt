package hu.mobilclient.memo.fragments.interfaces.user

import java.util.*

interface IUserUpdateHandler {
    fun onUserUpdated(userId: UUID)
}