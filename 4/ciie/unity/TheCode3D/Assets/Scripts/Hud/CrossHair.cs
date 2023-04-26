using UnityEngine;
using UnityEngine.UI;

public class CrossHair : MonoBehaviour {

    public float restingSize = 75;
    public float maxSize = 125;
    public float speed = 12;

    private RectTransform crossHair;
    private float currentSize;

    private void Start() {
        crossHair = GetComponent<RectTransform>();
    }

    // Actualizamos tamaño del crosshair
    private void Update() {
        if (isMoving())
            currentSize = Mathf.Lerp(currentSize, maxSize, Time.deltaTime * speed);
        else 
            currentSize = Mathf.Lerp(currentSize, restingSize, Time.deltaTime * speed);

        // Draws Current Size
        crossHair.sizeDelta = new Vector2(currentSize, currentSize);
    }

    public bool isMoving() {
        return (Input.GetAxis("Horizontal") != 0) || (Input.GetAxis("Vertical") != 0);
    }

}