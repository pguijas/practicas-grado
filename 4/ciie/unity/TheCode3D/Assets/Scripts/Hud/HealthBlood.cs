using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(Image))]
public class HealthBlood : MonoBehaviour{

    private Image bloodimg;
    private float maxHealth;

    // Inicialización
    private void Start(){
        EventManager.instance.UpdateLifeEvent.AddListener(UpdateBlood);
        bloodimg = GetComponent<Image>();
        maxHealth = GameObject.FindGameObjectWithTag("Player").GetComponent<PlayerController>().health;
    }

    private void UpdateBlood(float life){
        Color color = bloodimg.color;
        color.a = 1 - (life / maxHealth);
        bloodimg.color = color;
    }
}
