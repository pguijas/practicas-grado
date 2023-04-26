using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class Hud : MonoBehaviour{

    public TMP_Text currentBullets;
    public TMP_Text totalBullets;
    public Image imageGun;
    public Sprite[] sprites = new Sprite[3];

    private void Start(){
        EventManager.instance.UpdateBulletsEvent.AddListener(UpdateBullets);
    }

    void OnDisable(){
        EventManager.instance.UpdateBulletsEvent.RemoveListener(UpdateBullets);
    }

    public void UpdateBullets(int gun, int current, int total){
        // Gun 
        imageGun.sprite = sprites[gun];
 
        // Current
        if (current == -1)
            currentBullets.text = "--";
        else
            currentBullets.text = current.ToString();

        // Total
        if (total == 0)
            totalBullets.color = Color.red;
        else
            totalBullets.color = Color.white;

        totalBullets.text = total.ToString();
    }

}
