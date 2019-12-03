package hu.mobilclient.memo.fragments.interfaces.user

import java.util.*

interface IUserDeletionHandler {
    fun onUserDeleted(userId: UUID)
}