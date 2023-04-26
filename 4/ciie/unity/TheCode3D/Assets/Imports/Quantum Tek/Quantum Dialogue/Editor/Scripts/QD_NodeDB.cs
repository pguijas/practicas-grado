using System.Collections.Generic;
using UnityEngine;

namespace QuantumTek.QuantumDialogue.Editor
{
    [CreateAssetMenu(menuName = "Quantum Tek/Quantum Dialogue/Node Database")]
    public class QD_NodeDB : ScriptableObject
    {
        public QD_Dialogue DataDB;
        public int NextID = 0;
        public List<QD_Node> Nodes = new List<QD_Node>();
        public List<QD_SpeakerNode> SpeakerNodes = new List<QD_SpeakerNode>();
        public List<QD_MessageNode> MessageNodes = new List<QD_MessageNode>();
        public List<QD_ConversationNode> ConversationNodes = new List<QD_ConversationNode>();
        public List<QD_ChoiceNode> ChoiceNodes = new List<QD_ChoiceNode>();

        /// <summary>
        /// Returns a node with the given id.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public QD_Node GetNode(int id)
        {
            foreach (var node in Nodes)
                if (node.ID == id)
                    return node;
            return null;
        }
        /// <summary>
        /// Returns a node with the given id.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public QD_SpeakerNode GetSpeakerNode(int id)
        {
            foreach (var node in SpeakerNodes)
                if (node.ID == id)
                    return node;
            return null;
        }
        /// <summary>
        /// Returns a node with the given id.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public QD_MessageNode GetMessageNode(int id)
        {
            foreach (var node in MessageNodes)
                if (node.ID == id)
                    return node;
            return null;
        }
        /// <summary>
        /// Returns a node with the given id.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public QD_ConversationNode GetConversationNode(int id)
        {
            foreach (var node in ConversationNodes)
                if (node.ID == id)
                    return node;
            return null;
        }
        /// <summary>
        /// Returns a node with the given id.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public QD_ChoiceNode GetChoiceNode(int id)
        {
            foreach (var node in ChoiceNodes)
                if (node.ID == id)
                    return node;
            return null;
        }

        /// <summary>
        /// Returns the index of a node in the list.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public int GetNodeIndex(int id)
        {
            int nodeCount = Nodes.Count;
            for (int i = 0; i < nodeCount; ++i)
                if (Nodes[i].ID == id)
                    return i;
            return -1;
        }
        /// <summary>
        /// Returns the index of a node in the list.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public int GetSpeakerNodeIndex(int id)
        {
            int nodeCount = SpeakerNodes.Count;
            for (int i = 0; i < nodeCount; ++i)
                if (SpeakerNodes[i].ID == id)
                    return i;
            return -1;
        }
        /// <summary>
        /// Returns the index of a node in the list.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public int GetMessageNodeIndex(int id)
        {
            int nodeCount = MessageNodes.Count;
            for (int i = 0; i < nodeCount; ++i)
                if (MessageNodes[i].ID == id)
                    return i;
            return -1;
        }
        /// <summary>
        /// Returns the index of a node in the list.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public int GetConversationNodeIndex(int id)
        {
            int nodeCount = ConversationNodes.Count;
            for (int i = 0; i < nodeCount; ++i)
                if (ConversationNodes[i].ID == id)
                    return i;
            return -1;
        }
        /// <summary>
        /// Returns the index of a node in the list.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public int GetChoiceNodeIndex(int id)
        {
            int nodeCount = ChoiceNodes.Count;
            for (int i = 0; i < nodeCount; ++i)
                if (ChoiceNodes[i].ID == id)
                    return i;
            return -1;
        }

        /// <summary>
        /// Creates a new node of the given type at the given position.
        /// </summary>
        /// <param name="type"></param>
        /// <param name="position"></param>
        public void CreateNode(QD_NodeType type, Vector2 position)
        {
            int id = NextID++;
            var node = new QD_Node(id, type, position.x, position.y);

            if (type == QD_NodeType.Speaker)
            {
                var speakerNode = new QD_SpeakerNode(id, type, position.x, position.y);

                node.Window.width = speakerNode.Window.width;
                node.Window.height = speakerNode.Window.height;
                node.Inputs = speakerNode.CloneKnobs(QD_KnobType.Input);
                node.Outputs = speakerNode.CloneKnobs(QD_KnobType.Output);
                node.AllowedInputs = speakerNode.AllowedInputs;
                node.AllowedOutputs = speakerNode.AllowedOutputs;

                var speakerData = new QD_Speaker(id);
                DataDB.Speakers.Add(speakerData);
                speakerNode.Data = speakerData;

                SpeakerNodes.Add(speakerNode);
            }
            else if (type == QD_NodeType.Message)
            {
                var messageNode = new QD_MessageNode(id, type, position.x, position.y);

                node.Window.width = messageNode.Window.width;
                node.Window.height = messageNode.Window.height;
                node.Inputs = messageNode.CloneKnobs(QD_KnobType.Input);
                node.Outputs = messageNode.CloneKnobs(QD_KnobType.Output);
                node.AllowedInputs = messageNode.AllowedInputs;
                node.AllowedOutputs = messageNode.AllowedOutputs;

                var messageData = new QD_Message(id);
                DataDB.Messages.Add(messageData);
                messageNode.Data = messageData;

                MessageNodes.Add(messageNode);
            }
            else if (type == QD_NodeType.Conversation)
            {
                var conversationNode = new QD_ConversationNode(id, type, position.x, position.y);

                node.Window.width = conversationNode.Window.width;
                node.Window.height = conversationNode.Window.height;
                node.Inputs = conversationNode.CloneKnobs(QD_KnobType.Input);
                node.Outputs = conversationNode.CloneKnobs(QD_KnobType.Output);
                node.AllowedInputs = conversationNode.AllowedInputs;
                node.AllowedOutputs = conversationNode.AllowedOutputs;

                var conversationData = new QD_Conversation(id);
                DataDB.Conversations.Add(conversationData);
                conversationNode.Data = conversationData;

                ConversationNodes.Add(conversationNode);
            }
            else if (type == QD_NodeType.Choice)
            {
                var choiceNode = new QD_ChoiceNode(id, type, position.x, position.y);

                node.Window.width = choiceNode.Window.width;
                node.Window.height = choiceNode.Window.height;
                node.Inputs = choiceNode.CloneKnobs(QD_KnobType.Input);
                node.Outputs = choiceNode.CloneKnobs(QD_KnobType.Output);
                node.AllowedInputs = choiceNode.AllowedInputs;
                node.AllowedOutputs = choiceNode.AllowedOutputs;

                var choiceData = new QD_Choice(id);
                DataDB.Choices.Add(choiceData);
                choiceNode.Data = choiceData;

                ChoiceNodes.Add(choiceNode);
            }

            Nodes.Add(node);
        }
    }
}