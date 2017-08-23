package org.gammf.collabora.database.messages

import org.gammf.collabora.util._

/**
  * A trait for query messages
  */
trait QueryMessage

/**
  * A trait for notes query. A message contains the note, the collaboration id and the user who have performed the update.
  */
trait QueryNoteMessage extends QueryMessage {
  def note: Note
  def collaborationID: String
  def userID: String
}

case class InsertNoteMessage(note: Note, collaborationID: String, userID: String) extends QueryNoteMessage
case class UpdateNoteMessage(note: Note, collaborationID: String, userID: String) extends QueryNoteMessage
case class DeleteNoteMessage(note: Note, collaborationID: String, userID: String) extends QueryNoteMessage

/**
  * A trait for module query. A message contains the module, the collaboration id and the user who have performed the update.
  */
trait QueryModuleMessage extends QueryMessage {
  def module: Module
  def collaborationID: String
  def userID: String
}

case class InsertModuleMessage(module: Module, collaborationID: String, userID: String) extends QueryModuleMessage
case class UpdateModuleMessage(module: Module, collaborationID: String, userID: String) extends QueryModuleMessage
case class DeleteModuleMessage(module: Module, collaborationID: String, userID: String) extends QueryModuleMessage

/**
  * A trait for collaboration query. A message contains the collaboration and the user who have performed the update.
  */
trait QueryCollaborationMessage extends QueryMessage {
  def collaboration: Collaboration
  def userID: String
}

case class InsertCollaborationMessage(collaboration: Collaboration, userID: String) extends QueryCollaborationMessage
case class UpdateCollaborationMessage(collaboration: Collaboration, userID: String) extends QueryCollaborationMessage
case class DeleteCollaborationMessage(collaboration: Collaboration, userID: String) extends QueryCollaborationMessage

/**
  * A trait for user query. A message contains the user, the collaboration id and the user who have performed the update.
  */
trait QueryUserMessage extends QueryMessage {
  def user: CollaborationUser
  def collaborationID: String
  def userID: String
}

case class InsertUserMessage(user: CollaborationUser, collaborationID: String, userID: String) extends QueryUserMessage
case class UpdateUserMessage(user: CollaborationUser, collaborationID: String, userID: String) extends QueryUserMessage
case class DeleteUserMessage(user: CollaborationUser, collaborationID: String, userID: String) extends QueryUserMessage