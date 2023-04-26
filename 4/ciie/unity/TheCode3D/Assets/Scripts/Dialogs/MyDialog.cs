using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using QuantumTek.QuantumDialogue;

public class MyDialog : MonoBehaviour{
        public QD_DialogueHandler handler;
        public TextMeshProUGUI speakerName;
        public TextMeshProUGUI messageText;
        public Transform choices;
        public TextMeshProUGUI choiceTemplate;
        public string dialogo;

        private bool ended;

        private void Awake(){
            handler.SetConversation(dialogo);
            SetText();
        }

        // Checkea si si acabo o comprueba la entrada del usuario (next)
        private void Update(){
            if (ended){
                gameObject.SetActive(false); 
                EventManager.instance.DialogEndEvent.Invoke();
                return;
            }
            
            if (handler.currentMessageInfo.Type == QD_NodeType.Message && Input.GetKeyUp(KeyCode.Return))
                Next();
        }


        private void SetText(){
            // Vacía txto de mensaje
            speakerName.text = "";
            messageText.gameObject.SetActive(false);
            messageText.text = "";

            // Si acabó, no hace nada
            if (ended)
                return;

            // Muestra mensaje (si hay) 
            if (handler.currentMessageInfo.Type == QD_NodeType.Message){
                QD_Message message = handler.GetMessage();
                speakerName.text = message.SpeakerName;
                messageText.text = message.MessageText;
                messageText.gameObject.SetActive(true);

            }
        }

        // Se pasa al siguiente mensaje
        public void Next(int choice = -1){
            if (ended)
                return;
            
            handler.NextMessage(choice);
            SetText();

            // Ended si no hay mensaje
            if (handler.currentMessageInfo.ID < 0)
                ended = true;
        }
}
