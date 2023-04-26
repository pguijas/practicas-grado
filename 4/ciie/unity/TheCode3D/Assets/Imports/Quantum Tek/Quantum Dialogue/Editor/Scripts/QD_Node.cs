using System.Collections.Generic;
using UnityEngine;
using UnityEditor;

namespace QuantumTek.QuantumDialogue.Editor
{
    /// <summary>
    /// Used in defining what inputs and what outputs can connect, along with what types.
    /// </summary>
    [System.Serializable]
    public class QD_ConnectionRule
    {
        public QD_NodeType InputType;
        public int InputID;
        public QD_NodeType OutputType;
        public int OutputID;

        public QD_ConnectionRule(QD_NodeType inputType, int inputID, QD_NodeType outputType, int outputID)
        {
            InputType = inputType;
            InputID = inputID;
            OutputType = outputType;
            OutputID = outputID;
        }
    }

    [System.Serializable]
    public class QD_Node
    {
        public int ID;

        public QD_NodeType Type = QD_NodeType.Base;
        public Rect Window = new Rect(0, 0, 100, 100);
        public string WindowTitle => "Node";
        public List<QD_Knob> Inputs = new List<QD_Knob>
        {
            new QD_Knob(0, "Input 1", QD_KnobType.Input, 0),
            new QD_Knob(1, "Input 2", QD_KnobType.Input, 20, false)
        };
        public List<QD_Knob> Outputs = new List<QD_Knob>
        {
            new QD_Knob(0, "Output 1", QD_KnobType.Output, 0),
            new QD_Knob(1, "Output 2", QD_KnobType.Output, 20, false)
        };
        public List<QD_ConnectionRule> AllowedInputs = new List<QD_ConnectionRule>
        {
            new QD_ConnectionRule(QD_NodeType.Base, 0, QD_NodeType.Base, 0),
            new QD_ConnectionRule(QD_NodeType.Base, 1, QD_NodeType.Base, 1)
        };
        public List<QD_ConnectionRule> AllowedOutputs = new List<QD_ConnectionRule>
        {
            new QD_ConnectionRule(QD_NodeType.Base, 0, QD_NodeType.Base, 0),
            new QD_ConnectionRule(QD_NodeType.Base, 1, QD_NodeType.Base, 1)
        };

        public QD_Node(int id, QD_NodeType type, float x = 0, float y = 0)
        {
            ID = id;
            Type = type;
            Window.x = x;
            Window.y = y;
        }

        /// <summary>
        /// Returns a cloned list of knobs of the given type.
        /// </summary>
        /// <param name="type">The type of the knobs.</param>
        /// <returns></returns>
        public List<QD_Knob> CloneKnobs(QD_KnobType type)
        {
            List<QD_Knob> knobs = new List<QD_Knob>();

            if (type == QD_KnobType.Input)
                foreach (var knob in Inputs)
                    knobs.Add(new QD_Knob(knob.ID, knob.Name, knob.Type, knob.Y, knob.AllowMultipleConnections));
            else if (type == QD_KnobType.Output)
                foreach (var knob in Outputs)
                    knobs.Add(new QD_Knob(knob.ID, knob.Name, knob.Type, knob.Y, knob.AllowMultipleConnections));

            return knobs;
        }

        /// <summary>
        /// Returns the knob of the given type with the given id.
        /// </summary>
        /// <param name="type">The type of the knob.</param>
        /// <param name="id">The id of the knob.</param>
        /// <returns></returns>
        public QD_Knob GetKnob(QD_KnobType type, int id)
        {
            foreach (var knob in (type == QD_KnobType.Input ? Inputs : Outputs))
                if (knob.ID == id)
                    return knob;
            return null;
        }

        /// <summary>
        /// Returns the knob of the given type with the given name.
        /// </summary>
        /// <param name="type">The type of the knob.</param>
        /// <param name="name">The name of the knob.</param>
        /// <returns></returns>
        public QD_Knob GetKnob(QD_KnobType type, string name)
        {
            foreach (var knob in (type == QD_KnobType.Input ? Inputs : Outputs))
                if (knob.Name == name)
                    return knob;
            return null;
        }

        /// <summary>
        /// Returns whether or not a connection is allowed. Used before connecting.
        /// </summary>
        /// <param name="knobType">The knob type being connected to on this node.</param>
        /// <param name="knobID">The id of the knob being connected to on this node.</param>
        /// <param name="otherNodeType">The node type being connected to on the other node.</param>
        /// <param name="otherKnobType">The knob type being connected to on the other node.</param>
        /// <param name="otherKnobID">The id of the knob being connected to on the other node.</param>
        /// <returns></returns>
        public bool CanConnect(QD_KnobType knobType, int knobID, QD_NodeType otherNodeType, QD_KnobType otherKnobType, int otherKnobID)
        {
            bool canConnect = false;

            if (knobType == QD_KnobType.Input)
            {
                foreach (var rule in AllowedInputs)
                {
                    if (rule.InputType == Type && rule.InputID == knobID && rule.OutputType == otherNodeType && otherKnobType == QD_KnobType.Output && rule.OutputID == otherKnobID)
                    {
                        canConnect = true;
                        break;
                    }
                }
            }
            else if (knobType == QD_KnobType.Output)
            {
                foreach (var rule in AllowedOutputs)
                {
                    if (rule.OutputType == Type && rule.OutputID == knobID && rule.InputType == otherNodeType && otherKnobType == QD_KnobType.Input && rule.InputID == otherKnobID)
                    {
                        canConnect = true;
                        break;
                    }
                }
            }

            return canConnect;
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
        public virtual void OnConnect(QD_Dialogue dialogue, QD_NodeType connectionType, int connectionID, int connectionKnobID, int knobID, QD_KnobType knobType)
        {
            if (Type == QD_NodeType.Speaker)
                QD_DialogueEditor.db.GetSpeakerNode(ID).OnConnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
            else if (Type == QD_NodeType.Message)
                QD_DialogueEditor.db.GetMessageNode(ID).OnConnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
            else if (Type == QD_NodeType.Conversation)
                QD_DialogueEditor.db.GetConversationNode(ID).OnConnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
            else if (Type == QD_NodeType.Choice)
                QD_DialogueEditor.db.GetChoiceNode(ID).OnConnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
        }

        /// <summary>
        /// The function called to modify the node's data whenever a node disconnects.
        /// </summary>
        /// <param name="dialogue">The dialogue data.</param>
        /// <param name="connectionType">The type of the connecting node.</param>
        /// <param name="connectionID">The id of the connected node.</param>
        public virtual void OnDisconnect(QD_Dialogue dialogue, QD_NodeType connectionType, int connectionID)
        {
            if (Type == QD_NodeType.Speaker)
                QD_DialogueEditor.db.GetSpeakerNode(ID).OnDisconnect(dialogue, connectionType, connectionID);
            else if (Type == QD_NodeType.Message)
                QD_DialogueEditor.db.GetMessageNode(ID).OnDisconnect(dialogue, connectionType, connectionID);
            else if (Type == QD_NodeType.Conversation)
                QD_DialogueEditor.db.GetConversationNode(ID).OnDisconnect(dialogue, connectionType, connectionID);
            else if (Type == QD_NodeType.Choice)
                QD_DialogueEditor.db.GetChoiceNode(ID).OnDisconnect(dialogue, connectionType, connectionID);
        }

        /// <summary>
        /// The function called to modify the node's data whenever a node disconnects.
        /// </summary>
        /// <param name="dialogue">The dialogue data.</param>
        /// <param name="connectionType">The type of the connecting node.</param>
        /// <param name="connectionID">The id of the connected node.</param>
        /// <param name="knobID">The id of the knob.</param>
        /// <param name="knobType">The type of the knob.</param>
        public virtual void OnDisconnect(QD_Dialogue dialogue, QD_NodeType connectionType, int connectionID, int knobID, QD_KnobType knobType)
        {
            if (Type == QD_NodeType.Speaker)
                QD_DialogueEditor.db.GetSpeakerNode(ID).OnDisconnect(dialogue, connectionType, connectionID, knobID, knobType);
            else if (Type == QD_NodeType.Message)
                QD_DialogueEditor.db.GetMessageNode(ID).OnDisconnect(dialogue, connectionType, connectionID, knobID, knobType);
            else if (Type == QD_NodeType.Conversation)
                QD_DialogueEditor.db.GetConversationNode(ID).OnDisconnect(dialogue, connectionType, connectionID, knobID, knobType);
            else if (Type == QD_NodeType.Choice)
                QD_DialogueEditor.db.GetChoiceNode(ID).OnDisconnect(dialogue, connectionType, connectionID, knobID, knobType);
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
        public virtual void OnDisconnect(QD_Dialogue dialogue, QD_NodeType connectionType, int connectionID, int connectionKnobID, int knobID, QD_KnobType knobType)
        {
            if (Type == QD_NodeType.Speaker)
                QD_DialogueEditor.db.GetSpeakerNode(ID).OnDisconnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
            else if (Type == QD_NodeType.Message)
                QD_DialogueEditor.db.GetMessageNode(ID).OnDisconnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
            else if (Type == QD_NodeType.Conversation)
                QD_DialogueEditor.db.GetConversationNode(ID).OnDisconnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
            else if (Type == QD_NodeType.Choice)
                QD_DialogueEditor.db.GetChoiceNode(ID).OnDisconnect(dialogue, connectionType, connectionID, connectionKnobID, knobID, knobType);
        }

        /// <summary>
        /// Connects a knob from one node to a knob on this node.
        /// </summary>
        /// <param name="connectionID">The id of the connecting node.</param>
        /// <param name="connectionKnobID">The id of the connecting knob.</param>
        /// <param name="knobID">The id of this node's knob.</param>
        /// <param name="knobType">The type of this node's knob.</param>
        public void ConnectNode(int connectionID, int connectionKnobID, int knobID, QD_KnobType knobType)
        {
            QD_Knob knob = GetKnob(knobType, knobID);
            if (!knob.AllowMultipleConnections && knob.Connections.Count > 0)
                return;

            if (!knob.Connections.ContainsKey(connectionID))
                knob.Connections.Add(connectionID, new List<int> { connectionKnobID });
            else if (!knob.Connections.Get(connectionID).Contains(connectionKnobID))
                knob.Connections.Get(connectionID).Add(connectionKnobID);
            OnConnect(QD_DialogueEditor.db.DataDB, QD_DialogueEditor.db.GetNode(connectionID).Type, connectionID, connectionKnobID, knobID, knobType);
        }

        /// <summary>
        /// Disconnects a node.
        /// </summary>
        /// <param name="connectionID">The id of the connected node.</param>
        public void DisconnectNode(int connectionID)
        {
            foreach (var knob in Inputs)
                knob.Disconnect(connectionID);
            foreach (var knob in Outputs)
                knob.Disconnect(connectionID);
            OnDisconnect(QD_DialogueEditor.db.DataDB, QD_DialogueEditor.db.GetNode(connectionID).Type, connectionID);
        }

        /// <summary>
        /// Disconnects a node from a knob.
        /// </summary>
        /// <param name="connectionID">The id of the connected node.</param>
        /// <param name="knobID">The id of the knob.</param>
        /// <param name="knobType">The type of the knob.</param>
        public void DisconnectNode(int connectionID, int knobID, QD_KnobType knobType)
        {
            QD_Knob knob = GetKnob(knobType, knobID);
            knob.Disconnect(connectionID);
            OnDisconnect(QD_DialogueEditor.db.DataDB, QD_DialogueEditor.db.GetNode(connectionID).Type, connectionID, knobID, knobType);
        }

        /// <summary>
        /// Disconnects a node's knob from a knob.
        /// </summary>
        /// <param name="connectionID">The id of the connected node.</param>
        /// <param name="connectionKnobID">The id of the connected knob.</param>
        /// <param name="knobID">The id of the knob.</param>
        /// <param name="knobType">The type of the knob.</param>
        public void DisconnectNode(int connectionID, int connectionKnobID, int knobID, QD_KnobType knobType)
        {
            QD_Knob knob = GetKnob(knobType, knobID);
            knob.Disconnect(connectionID, connectionKnobID);
            OnDisconnect(QD_DialogueEditor.db.DataDB, QD_DialogueEditor.db.GetNode(connectionID).Type, connectionID, connectionKnobID, knobID, knobType);
        }

        public virtual void DrawWindow(int id)
        {
            GUI.DragWindow();
        }

        public Rect InputKnob(int index)
        {
            int inputCount = Inputs.Count;
            float maxInputSize = inputCount > 0 ? Window.height / (inputCount + 1) : 0;
            float inputSize = Mathf.Min(15, maxInputSize);
            return new Rect(Window.x - inputSize + 2, Window.y + Inputs[index].Y, inputSize, inputSize);
        }

        public Rect OutputKnob(int index)
        {
            int outputCount = Outputs.Count;
            float maxOutputSize = outputCount > 0 ? Window.height / (outputCount + 1) : 0;
            float outputSize = Mathf.Min(15, maxOutputSize);
            return new Rect(Window.x + Window.width - 2, Window.y + Outputs[index].Y, outputSize, outputSize);
        }

        public void DrawKnobs()
        {
            int inputCount = Inputs.Count;
            for (int i = 0; i < inputCount; ++i)
                GUI.Button(InputKnob(i), "");

            int outputCount = Outputs.Count;
            for (int i = 0; i < outputCount; ++i)
                GUI.Button(OutputKnob(i), "");
        }
    }
}