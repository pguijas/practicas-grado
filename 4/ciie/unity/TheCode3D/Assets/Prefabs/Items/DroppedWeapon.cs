using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;


public class DroppedWeapon : MonoBehaviour{

    [SerializeField]
    public float distance=4;
    public WeaponController weaponPrefab;
    public AudioSource pickup_sound;
    private bool txtVisualized = false;
    private GameObject player;
    private GameObject short_text;

    void Start(){
        player = GameObject.FindWithTag("Player");
        short_text = GameObject.FindWithTag("short_text");
    }

    void Update(){
        Vector3 distanceToPlayer = player.transform.position - transform.position;
        // Si se esta lo suficiente cerca se podrá pickear el arma
        if (distanceToPlayer.magnitude < distance){
            // Text Visualization
            if (short_text.GetComponent<TextMeshProUGUI>().text == ""){
                short_text.GetComponent<TextMeshProUGUI>().text = "Presiona E para equipar el arma";
                txtVisualized = true;
            }

            // Pick
            if (Input.GetKeyDown(KeyCode.E)){
                if (txtVisualized){
                    player.GetComponent<WeaponManager>().PickWeapon(weaponPrefab);
                    short_text.GetComponent<TextMeshProUGUI>().text = "";
                    if (pickup_sound != null)
                        pickup_sound.Play();
                    Destroy(gameObject);
                }
            }
        }


        else{
            if (txtVisualized){
                txtVisualized = false;
                short_text.GetComponent<TextMeshProUGUI>().text = "";
            }
        }
    }

    public void SetBullets(int mag, int totalBullets){
        weaponPrefab.currentMag = mag;
        weaponPrefab.bullets = totalBullets;
    }
}
