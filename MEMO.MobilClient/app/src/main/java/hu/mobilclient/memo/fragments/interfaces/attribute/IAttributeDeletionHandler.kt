package hu.mobilclient.memo.fragments.interfaces.attribute

import java.util.*

interface IAttributeDeletionHandler {
    fun onAttributeDeleted(attributeId: UUID)
}