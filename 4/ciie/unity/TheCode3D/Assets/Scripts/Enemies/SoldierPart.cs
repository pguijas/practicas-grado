using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SoldierPart : MonoBehaviour{

    public GameObject soldier;
    
    public void DoDamage(float damage) {
        soldier.GetComponent<SoldierIA>().TakeDamage(damage);
    }
}
