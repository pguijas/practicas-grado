using System.Collections.Generic;
using UnityEngine;
using UnityEditor;

namespace QuantumTek.QuantumDialogue.Editor
{
    public class QD_DialogueEditor : EditorWindow
    {
        public static QD_DialogueEditor editor;
        public static GUISkin skin;
        public static QD_NodeDB db;
        public Vector2 mouse;
        public bool connectingNodes;
        public QD_Node selectedNode;
        public int selectedKnob = -1;
        public QD_KnobType knobType;
        public Vector2 scroll;

        [MenuItem("Window/Quantum Dialogue/Dialogue Editor")]
        public static void ShowWindow()
        { editor = GetWindow<QD_DialogueEditor>("Dialogue Editor"); }

        private void OnGUI()
        {
            if (!editor) editor = this;
            SelectDB();
            skin = Resources.Load<GUISkin>("Node Editor Skin");
            GUI.skin = skin;
            float eWidth = editor.position.width;
            float eHeight = editor.position.height;

            if (!db) EditorGUILayout.HelpBox("Select an object with a QD_NodeDB script.", MessageType.Info);
            else
            {
                Event e = Event.current;
                mouse = e.mousePosition;
                int nodeCount = db.Nodes.Count;

                // Mouse click events, taken from https://forum.unity.com/threads/simple-node-editor.189230/
                if (e.type == EventType.MouseDown && e.button == 1 && !connectingNodes)
                {
                    bool clicked = false;
                    int selectedIndex = -1;
                    bool knobClicked = false;
                    int knobSelectedIndex = -1;

                    for (int i = 0; i < nodeCount; ++i)
                    {
                        QD_Node node = db.Nodes[i];
                        if (node.Window.Contains(mouse + scroll))
                        {
                            selectedIndex = i;
                            clicked = true;
                            break;
                        }
                        else
                        {
                            int inputCount = node.Inputs.Count;
                            for (int j = 0; j < inputCount; ++j)
                            {
                                if (node.InputKnob(j).Contains(mouse + scroll))
                                {
                                    selectedIndex = i;
                                    clicked = true;
                                    knobSelectedIndex = j;
                                    knobClicked = true;
                                    knobType = QD_KnobType.Input;
                                    selectedKnob = j;
                                    break;
                                }
                            }

                            if (knobClicked) break;

                            int outputCount = node.Outputs.Count;
                            for (int j = 0; j < outputCount; ++j)
                            {
                                if (node.OutputKnob(j).Contains(mouse + scroll))
                                {
                                    selectedIndex = i;
                                    clicked = true;
                                    knobSelectedIndex = j;
                                    knobClicked = true;
                                    knobType = QD_KnobType.Output;
                                    selectedKnob = j;
                                    break;
                                }
                            }

                            if (knobClicked) break;
                        }
                    }

                    if (!clicked)
                    {
                        GenericMenu menu = new GenericMenu();
                        menu.AddItem(new GUIContent("Add Conversation"), false, ContextCallback, "Add Conversation");
                        menu.AddItem(new GUIContent("Add Speaker"), false, ContextCallback, "Add Speaker");
                        menu.AddItem(new GUIContent("Add Message"), false, ContextCallback, "Add Message");
                        menu.AddItem(new GUIContent("Add Choice"), false, ContextCallback, "Add Choice");
                        menu.ShowAsContext();
                        e.Use();
                    }
                    else
                    {
                        GenericMenu menu = new GenericMenu();
                        if (!knobClicked)
                        {
                            menu.AddItem(new GUIContent("Detach All Connected Nodes"), false, ContextCallback, "Detach All Connected Nodes");
                            menu.AddItem(new GUIContent("Delete Node"), false, ContextCallback, "Delete Node");
                        }
                        else
                        {
                            menu.AddItem(new GUIContent("Attach to Node"), false, ContextCallback, "Attach to Node");
                            menu.AddItem(new GUIContent("Detach Connected Nodes"), false, ContextCallback, "Detach Connected Nodes");
                        }
                        menu.ShowAsContext();
                        e.Use();
                        selectedNode = db.Nodes[selectedIndex];
                    }
                }
                else if (e.type == EventType.MouseDown && e.button == 0 && connectingNodes)
                {
                    bool clicked = false;
                    int selectedIndex = -1;
                    bool knobClicked = false;
                    int knobSelectedIndex = -1;
                    QD_KnobType clickedKnobType = QD_KnobType.Input;

                    for (int i = 0; i < nodeCount; ++i)
                    {
                        QD_Node node = db.Nodes[i];
                        int inputCount = node.Inputs.Count;
                        for (int j = 0; j < inputCount; ++j)
                        {
                            if (node.InputKnob(j).Contains(mouse + scroll))
                            {
                                selectedIndex = i;
                                clicked = true;
                                knobSelectedIndex = j;
                                knobClicked = true;
                                clickedKnobType = QD_KnobType.Input;
                                break;
                            }
                        }

                        if (knobClicked) break;

                        int outputCount = node.Outputs.Count;
                        for (int j = 0; j < outputCount; ++j)
                        {
                            if (node.OutputKnob(j).Contains(mouse + scroll))
                            {
                                selectedIndex = i;
                                clicked = true;
                                knobSelectedIndex = j;
                                knobClicked = true;
                                clickedKnobType = QD_KnobType.Output;
                                break;
                            }
                        }

                        if (knobClicked) break;
                    }

                    if (clicked)
                    {
                        EditorUtility.SetDirty(db);
                        if (knobType == QD_KnobType.Input && clickedKnobType == QD_KnobType.Output)
                        {
                            QD_Node outputNode = db.Nodes[selectedIndex];
                            QD_Knob input = selectedNode.Inputs[selectedKnob];
                            QD_Knob output = outputNode.Outputs[knobSelectedIndex];
                            if (selectedNode.ID != outputNode.ID &&
                                (!output.Connections.ContainsKey(selectedNode.ID) || !output.Connections.Get(selectedNode.ID).Contains(selectedKnob)) &&
                                (!input.Connections.ContainsKey(outputNode.ID) || !input.Connections.Get(outputNode.ID).Contains(knobSelectedIndex)) &&
                                (output.AllowMultipleConnections || output.Connections.Count == 0) &&
                                (input.AllowMultipleConnections || input.Connections.Count == 0) &&
                                selectedNode.CanConnect(knobType, selectedKnob, outputNode.Type, QD_KnobType.Output, knobSelectedIndex)
                                )
                            {
                                selectedNode.ConnectNode(outputNode.ID, knobSelectedIndex, selectedKnob, QD_KnobType.Input);
                                outputNode.ConnectNode(selectedNode.ID, selectedKnob, knobSelectedIndex, QD_KnobType.Output);
                            }
                        }
                        else if (knobType == QD_KnobType.Output && clickedKnobType == QD_KnobType.Input)
                        {
                            QD_Node inputNode = db.Nodes[selectedIndex];
                            QD_Knob input = inputNode.Inputs[knobSelectedIndex];
                            QD_Knob output = selectedNode.Outputs[selectedKnob];
                            if (selectedNode.ID != inputNode.ID &&
                                (!output.Connections.ContainsKey(inputNode.ID) || !output.Connections.Get(inputNode.ID).Contains(knobSelectedIndex)) &&
                                (!input.Connections.ContainsKey(selectedNode.ID) || !input.Connections.Get(selectedNode.ID).Contains(selectedKnob)) &&
                                (output.AllowMultipleConnections || output.Connections.Count == 0) &&
                                (input.AllowMultipleConnections || input.Connections.Count == 0) &&
                                selectedNode.CanConnect(knobType, selectedKnob, inputNode.Type, QD_KnobType.Input, knobSelectedIndex)
                                )
                            {
                                selectedNode.ConnectNode(inputNode.ID, knobSelectedIndex, selectedKnob, QD_KnobType.Output);
                                inputNode.ConnectNode(selectedNode.ID, selectedKnob, knobSelectedIndex, QD_KnobType.Input);
                            }
                        }
                    }

                    connectingNodes = false;
                    selectedNode = null;
                    knobClicked = false;
                    selectedKnob = -1;
                    e.Use();
                }
                if (connectingNodes && selectedNode != null)
                {
                    Rect mouseRect = new Rect(e.mousePosition.x, e.mousePosition.y, 10, 10);
                    Rect knobRect = knobType == QD_KnobType.Input ? selectedNode.InputKnob(selectedKnob) : selectedNode.OutputKnob(selectedKnob);
                    knobRect.position += new Vector2(knobRect.width / 2, knobRect.height / 2) - scroll;
                    DrawNodeCurve(knobType == QD_KnobType.Output ? knobRect : mouseRect, knobType == QD_KnobType.Output ? mouseRect : knobRect, false);
                    Repaint();
                }

                GUILayout.BeginArea(new Rect(-scroll, new Vector2(5000, 5000)));

                for (int i = 0; i < nodeCount; ++i)
                {
                    QD_Node node = db.Nodes[i];
                    int outputCount = node.Outputs.Count;
                    for (int j = 0; j < outputCount; ++j)
                    {
                        QD_Knob output = node.Outputs[j];
                        if (output.Connections.Count > 0)
                        {
                            foreach (KeyValuePair<int, List<int>> connection in output.Connections.dictionary)
                            {
                                int connectionCount = connection.Value.Count;
                                for (int k = 0; k < connectionCount; ++k)
                                    DrawNodeCurve(node.OutputKnob(j), db.GetNode(connection.Key).InputKnob(connection.Value[k]), true);
                            }
                        }
                    }
                }

                BeginWindows();
                for (int i = 0; i < nodeCount; ++i)
                {
                    QD_Node node = db.Nodes[i];
                    if (node.Type == QD_NodeType.Speaker)
                    {
                        QD_SpeakerNode realNode = db.GetSpeakerNode(node.ID);
                        node.Window = GUI.Window(node.ID, node.Window, realNode.DrawWindow, realNode.WindowTitle);
                    }
                    else if (node.Type == QD_NodeType.Message)
                    {
                        QD_MessageNode realNode = db.GetMessageNode(node.ID);
                        node.Window = GUI.Window(node.ID, node.Window, realNode.DrawWindow, realNode.WindowTitle);
                    }
                    else if (node.Type == QD_NodeType.Conversation)
                    {
                        QD_ConversationNode realNode = db.GetConversationNode(node.ID);
                        node.Window = GUI.Window(node.ID, node.Window, realNode.DrawWindow, realNode.WindowTitle);
                    }
                    else if (node.Type == QD_NodeType.Choice)
                    {
                        QD_ChoiceNode realNode = db.GetChoiceNode(node.ID);
                        node.Window = GUI.Window(node.ID, node.Window, realNode.DrawWindow, realNode.WindowTitle);
                    }
                    node.DrawKnobs();
                }
                EndWindows();

                GUILayout.EndArea();

                scroll = new Vector2(
                    GUI.HorizontalScrollbar(new Rect(0, eHeight - 15, eWidth - 15, eHeight - 5), scroll.x, eHeight, 0, 5000),
                    GUI.VerticalScrollbar(new Rect(eWidth - 15, 0, eWidth - 5, eHeight - 15), scroll.y, eHeight, 0, 5000)
                );
            }
        }

        public void SelectDB()
        {
            if (Selection.activeObject && Selection.activeObject.GetType() == typeof(QD_NodeDB))
                db = (QD_NodeDB)Selection.activeObject;
            else db = null;
        }

        // Taken from https://forum.unity.com/threads/simple-node-editor.189230/
        public void DrawNodeCurve(Rect start, Rect end, bool node)
        {
            Vector3 startPos = new Vector3(start.x + (node ? start.width : 0), start.y + (node ? start.height / 2 : 0), 0);
            Vector3 endPos = new Vector3(end.x, end.y + (node ? end.height / 2 : 0), 0);
            Vector3 startTan = startPos + Vector3.right * 50;
            Vector3 endTan = endPos + Vector3.left * 50;
            Handles.DrawBezier(startPos, endPos, startTan, endTan, skin.customStyles[3].normal.textColor, null, 3);
        }

        public void ContextCallback(object obj)
        {
            string command = obj.ToString();
            EditorUtility.SetDirty(db);
            EditorUtility.SetDirty(db.DataDB);

            if (command == "Add Conversation")
                db.CreateNode(QD_NodeType.Conversation, mouse + scroll);
            else if (command == "Add Speaker")
                db.CreateNode(QD_NodeType.Speaker, mouse + scroll);
            else if (command == "Add Message")
                db.CreateNode(QD_NodeType.Message, mouse + scroll);
            else if (command == "Add Choice")
                db.CreateNode(QD_NodeType.Choice, mouse + scroll);
            else if (command == "Attach to Node")
                connectingNodes = true;
            else if (command == "Detach All Connected Nodes")
                DetachNodes();
            else if (command == "Detach Connected Nodes")
                DetachNodes(selectedKnob, knobType);
            else if (command == "Delete Node")
            {
                DetachNodes();

                int i = db.GetNodeIndex(selectedNode.ID);
                QD_Node node = db.Nodes[i];
                if (node.Type == QD_NodeType.Speaker)
                    db.SpeakerNodes.RemoveAt(db.GetSpeakerNodeIndex(selectedNode.ID));
                else if (node.Type == QD_NodeType.Message)
                    db.MessageNodes.RemoveAt(db.GetMessageNodeIndex(selectedNode.ID));
                else if (node.Type == QD_NodeType.Conversation)
                    db.ConversationNodes.RemoveAt(db.GetConversationNodeIndex(selectedNode.ID));
                else if (node.Type == QD_NodeType.Choice)
                    db.ChoiceNodes.RemoveAt(db.GetChoiceNodeIndex(selectedNode.ID));
                db.Nodes.RemoveAt(i);
                if (node.Type == QD_NodeType.Speaker)
                {
                    foreach (var n in db.Nodes)
                    {
                        if (n.Type == QD_NodeType.Message)
                        {
                            QD_MessageNode messageNode = db.GetMessageNode(n.ID);
                            if (messageNode.Data.Speaker == node.ID)
                            {
                                messageNode.Data.Speaker = -1;
                                messageNode.Data.SpeakerName = "";
                            }
                        }
                    }

                    i = db.DataDB.GetSpeakerIndex(selectedNode.ID);
                    db.DataDB.Speakers.RemoveAt(i);
                }
                else if (node.Type == QD_NodeType.Message)
                {
                    i = db.DataDB.GetMessageIndex(selectedNode.ID);
                    db.DataDB.Messages.RemoveAt(i);
                }
                else if (node.Type == QD_NodeType.Conversation)
                {
                    i = db.DataDB.GetConversationIndex(selectedNode.ID);
                    db.DataDB.Conversations.RemoveAt(i);
                }
                else if (node.Type == QD_NodeType.Choice)
                {
                    i = db.DataDB.GetChoiceIndex(selectedNode.ID);
                    db.DataDB.Choices.RemoveAt(i);
                }
                if (db.Nodes.Count == 0)
                    db.NextID = 0;
            }
        }

        public void DetachNodes()
        {
            int inputCount = selectedNode.Inputs.Count;
            int outputCount = selectedNode.Outputs.Count;

            for (int i = 0; i < inputCount; ++i)
                DetachNodes(i, QD_KnobType.Input);
            for (int i = 0; i < outputCount; ++i)
                DetachNodes(i, QD_KnobType.Output);
        }
        public void DetachNodes(int knobID, QD_KnobType type)
        {
            QD_Knob knob = selectedNode.GetKnob(type, knobID);
            
            List<int> keys = new List<int>();
            if (knob.Connections.Count > 0)
            {
                foreach (KeyValuePair<int, List<int>> connection in knob.Connections.dictionary)
                {
                    int connectionCount = connection.Value.Count;
                    QD_Node conn = db.GetNode(connection.Key);
                    for (int j = 0; j < connectionCount; ++j)
                        conn.DisconnectNode(selectedNode.ID, connection.Value[j], type == QD_KnobType.Input ? QD_KnobType.Output : QD_KnobType.Input);
                    keys.Add(connection.Key);
                }
            }
            int keyCount = keys.Count;
            for (int j = 0; j < keyCount; ++j)
                selectedNode.DisconnectNode(keys[j], knobID, type);
        }
    }
}