using System.Collections.Generic;
using UnityEngine;
using UnityEditor;

namespace QuantumTek.QuantumDialogue.Editor
{
    [System.Serializable]
    public class QD_MessageNode : QD_Node
    {
        public new string WindowTitle => Data.SpeakerName;
        public QD_Message Data;

        public QD_MessageNode(int id, QD_NodeType type, float x = 0, float y = 0) : base(id, type, x, y)
        {
            Type = QD_NodeType.Message;
            Window = new Rect(0, 0, 200, 175);
            Inputs = new List<QD_Knob>
            {
                new QD_Knob(0, "Speaker", QD_KnobType.Input, 7.5f, false),
                new QD_Knob(1, "Previous", QD_KnobType.Input, 35, false)
            };
            Outputs = new List<QD_Knob>
            {
                new QD_Knob(0, "Speaker", QD_KnobType.Output, 7.5f),
                new QD_Knob(1, "Next", QD_KnobType.Output, 35, false)
            };
            AllowedInputs = new List<QD_ConnectionRule>
            {
                new QD_ConnectionRule(QD_NodeType.Message, 0, QD_NodeType.Speaker, 0),
                new QD_ConnectionRule(QD_NodeType.Message, 1, QD_NodeType.Message, 1),
                new QD_ConnectionRule(QD_NodeType.Message, 1, QD_NodeType.Conversation, 0),
                new QD_ConnectionRule(QD_NodeType.Message, 1, QD_NodeType.Choice, 0)
            };
            AllowedOutputs = new List<QD_ConnectionRule>
            {
                new QD_ConnectionRule(QD_NodeType.Message, 0, QD_NodeType.Message, 0),
                new QD_ConnectionRule(QD_NodeType.Message, 1, QD_NodeType.Message, 1),
                new QD_ConnectionRule(QD_NodeType.Choice, 0, QD_NodeType.Message, 1),
                new QD_ConnectionRule(QD_NodeType.Choice, 1, QD_NodeType.Message, 1),
                new QD_ConnectionRule(QD_NodeType.Choice, 2, QD_NodeType.Message, 1),
                new QD_ConnectionRule(QD_NodeType.Choice, 3, QD_NodeType.Message, 1),
                new QD_ConnectionRule(QD_NodeType.Choice, 4, QD_NodeType.Message, 1),
                new QD_ConnectionRule(QD_NodeType.Choice, 5, QD_NodeType.Message, 1)
            };
        }

        public override void DrawWindow(int id)
        {
            EditorGUI.BeginChangeCheck();
            EditorGUI.LabelField(new Rect(5, 32.5f, 190, 20), "Message", QD_DialogueEditor.skin.label);
            string fMessage = EditorGUI.TextArea(new Rect(5, 55, 190, 65), Data.MessageText, QD_DialogueEditor.skin.textArea);
            EditorGUI.LabelField(new Rect(5, 125, 190, 20), "Audio Clip", QD_DialogueEditor.skin.label);
            EditorGUILayout.Space(125);
            AudioClip fClip = (AudioClip)EditorGUILayout.ObjectField(Data.Clip, typeof(AudioClip), false);

            if (EditorGUI.EndChangeCheck())
            {
                EditorUtility.SetDirty(QD_DialogueEditor.db.DataDB);
                Data.MessageText = fMessage;
                Data.Clip = fClip;
                QD_DialogueEditor.db.DataDB.SetMessage(Data.ID, Data);
            }

            GUI.DragWindow();
        }

        /// <summary>
        /// The function called to modify the node's data whenever a node connects.
        /// </summary>
        /// <param name="dialogue">The dialogue data.</param>
        /// <param name="connectionType">The type of the connecting node.</param>
        /// <param name="connectionID">The id of the connecting node.</param>
        /// <param name="connectionKnobID">The id of the connecting knob.</param>
        /// <param name="knobID">The id of this node's knob.</param>
        /// <param name="knobType">The type of this node's knob.</param>
        public override void OnConnect(QD_Dialogue dialogue, QD_NodeType connectionType, int connectionID, int connectionKnobID, int knobID, QD_KnobType knobType)
        {
            Data.OnConnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
            QD_DialogueEditor.db.DataDB.SetMessage(Data.ID, Data);

            if (connectionType == QD_NodeType.Speaker && knobID == 0 && knobType == QD_KnobType.Input)
            {
                QD_Node message = QD_DialogueEditor.db.GetNode(ID);
                QD_Knob messageKnob = message.GetKnob(QD_KnobType.Output, 0);
                int nextMessageID = messageKnob.Connections.Count > 0 ? messageKnob.Connections.keys[0] : -1;
                QD_Message nextMessage = nextMessageID != -1 ? dialogue.GetMessage(nextMessageID) : null;
                while (nextMessageID != -1 && nextMessage != null)
                {
                    nextMessage.Speaker = connectionID;
                    nextMessage.SpeakerName = dialogue.GetSpeaker(connectionID).Name;
                    QD_DialogueEditor.db.DataDB.SetMessage(nextMessage.ID, nextMessage);

                    message = QD_DialogueEditor.db.GetNode(nextMessageID);
                    messageKnob = message.GetKnob(QD_KnobType.Output, 0);
                    nextMessageID = messageKnob.Connections.Count > 0 ? messageKnob.Connections.keys[0] : -1;
                    nextMessage = nextMessageID != -1 ? dialogue.GetMessage(nextMessageID) : null;
                }
            }
        }

        /// <summary>
        /// The function called to modify the node's data whenever a node disconnects.
        /// </summary>
        /// <param name="dialogue">The dialogue data.</param>
        /// <param name="connectionType">The type of the connecting node.</param>
        /// <param name="connectionID">The id of the connected node.</param>
        public override void OnDisconnect(QD_Dialogue dialogue, QD_NodeType connectionType, int connectionID)
        {
            Data.OnDisconnect(dialogue, connectionType, connectionID);
            QD_DialogueEditor.db.DataDB.SetMessage(Data.ID, Data);
        }

        /// <summary>
        /// The function called to modify the node's data whenever a node disconnects.
        /// </summary>
        /// <param name="dialogue">The dialogue data.</param>
        /// <param name="connectionType">The type of the connecting node.</param>
        /// <param name="connectionID">The id of the connected node.</param>
        /// <param name="knobID">The id of the knob.</param>
        /// <param name="knobType">The type of the knob.</param>
        public override void OnDisconnect(QD_Dialogue dialogue, QD_NodeType connectionType, int connectionID, int knobID, QD_KnobType knobType)
        {
            Data.OnDisconnect(dialogue, connectionType, connectionID, knobID, knobType);
            QD_DialogueEditor.db.DataDB.SetMessage(Data.ID, Data);
        }

        /// <summary>
        /// The function called to modify the node's data whenever a node disconnects.
        /// </summary>
        /// <param name="dialogue">The dialogue data.</param>
        /// <param name="connectionType">The type of the connecting node.</param>
        /// <param name="connectionID">The id of the connected node.</param>
        /// <param name="connectionKnobID">The id of the connected knob.</param>
        /// <param name="knobID">The id of the knob.</param>
        /// <param name="knobType">The type of the knob.</param>
        public override void OnDisconnect(QD_Dialogue dialogue, QD_NodeType connectionType, int connectionID, int connectionKnobID, int knobID, QD_KnobType knobType)
        {
            Data.OnDisconnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
            QD_DialogueEditor.db.DataDB.SetMessage(Data.ID, Data);
        }
    }
}