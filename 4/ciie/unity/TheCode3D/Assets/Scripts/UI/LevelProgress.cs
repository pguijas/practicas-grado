using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LevelProgress : MonoBehaviour{
   
    public List<GameObject> orderedLvlButtons;

    void Start(){
        int lvl = PlayerPrefs.GetInt("level", 0);
        for(int i = 0; i < orderedLvlButtons.Count; i++){
            orderedLvlButtons[i].SetActive(i <= lvl);
        }
    }

}
