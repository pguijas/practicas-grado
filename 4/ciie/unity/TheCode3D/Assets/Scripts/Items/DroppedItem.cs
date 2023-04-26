using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class DroppedItem : MonoBehaviour{

    public bool toogleLife_Ammo = true;
    public float distance=3;
    public AudioSource pickup_sound;
    private GameObject player;
    private GameObject short_text;
    
    void Start(){
        player = GameObject.FindWithTag("Player");
        short_text = GameObject.FindWithTag("short_text");
    }

    
    void Update(){
        Vector3 distanceToPlayer = player.transform.position - transform.position;
        // Si se esta lo suficiente cerca se aplicará el item automáticamente
        if (distanceToPlayer.magnitude < distance){
            // Show Test
            StartCoroutine(DoThings());
            distance=float.MaxValue;
        }
    }


    // Muestra texto y se encarga de 
    IEnumerator DoThings(){
        if (toogleLife_Ammo){
            short_text.GetComponent<TextMeshProUGUI>().text = "Recogidas Curaciones";
            Heal();
        } else{
            short_text.GetComponent<TextMeshProUGUI>().text = "Recogida Munición";
            Ammo();
        }

        yield return new WaitForSeconds(1); // Espera 1 segundo.

        short_text.GetComponent<TextMeshProUGUI>().text = "";
        if (pickup_sound != null)
            pickup_sound.Play();
        Destroy(gameObject);
    }

    private void Heal(){
        player.GetComponent<PlayerController>().SetMaxHealth(); 
    }

    private void Ammo(){
        player.GetComponent<WeaponManager>().SetMaxAmmo();
    }
}
