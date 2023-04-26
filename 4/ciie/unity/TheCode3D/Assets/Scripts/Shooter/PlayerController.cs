using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[RequireComponent(typeof(CharacterController))]
public class PlayerController : MonoBehaviour {

    [Header ("References")]
    public Camera playerCamera;
    [Header ("Physics")]
    public float gravityScale = -40f;
    [Header ("Player Settings")]
    public float walkSpeed = 5f;
    public float runSpeed = 20f;
    public float maxHealth = 100f;
    public float health = 100f;
    public float rotationSensibility = 1000f;
    public float jumpHeight = 1.9f;
    public AudioSource damage_sound;

    private bool run = false;
    private float cameraVerticalAngle;
    Vector3 moveInput = Vector3.zero;
    Vector2 rotationInput = Vector3.zero;
    CharacterController characterController;


    private void Awake() {
        Time.timeScale = 1f;
        Cursor.lockState = CursorLockMode.Locked;
        characterController = GetComponent<CharacterController>();
    }


    private void Update() {
        Look();
        Move();
    }


    public void SetMaxHealth(){
        health = maxHealth;
        EventManager.instance.UpdateLifeEvent.Invoke(health);
    }

    // Movimiento del jugador
    private void Move() {
        // detectamos is el jugador está en el suelo
        if (characterController.isGrounded) {
            // cogemos el input del teclado
            moveInput = new Vector3(Input.GetAxis("Horizontal"), 0f, Input.GetAxis("Vertical"));
            moveInput = Vector3.ClampMagnitude(moveInput, 1f);
            if (run)
                // si el flag de correr está activado, aplicamos la velocidad de correr
                moveInput = transform.TransformDirection(moveInput) * runSpeed;
            else
                // si no, la de caminar
                moveInput = transform.TransformDirection(moveInput) * walkSpeed;
            if (Input.GetButton("Jump"))
                moveInput.y = Mathf.Sqrt(jumpHeight * -2f * gravityScale);
        }
        // aplicamos fuerzas de gravedad en el eje y
        moveInput.y += gravityScale * Time.deltaTime;
        // aplicamos estas fuerzas al jugador
	    characterController.Move(moveInput * Time.deltaTime);
    }

    // Rotación del jugador
    private void Look() {
        // cogemos el movimiento del eje x e y
        rotationInput.x = Input.GetAxis("Mouse X") * rotationSensibility * Time.deltaTime;
        rotationInput.y = Input.GetAxis("Mouse Y") * rotationSensibility * Time.deltaTime;
        // la cámara mira hacia arriba o abajo
        cameraVerticalAngle = cameraVerticalAngle + rotationInput.y;
        cameraVerticalAngle = Mathf.Clamp(cameraVerticalAngle, -70f, 70f);
        // el jugador rota sobre si mismo
        transform.Rotate(Vector3.up * rotationInput.x);
        // movemos la cámara del jugador
        playerCamera.transform.localRotation = Quaternion.Euler(-cameraVerticalAngle, 0f, 0f);
    }


    public void Sprint() {
        run = true;
    }


    public void StopSprint() {
        run = false;
    }

    // Función de daño al jugador
    public void TakeDamage(float damage) {
        if (health - damage <= 0)
            health = 0;
        else 
            health -= damage;
        // animaciones de sonido
        if (damage_sound != null)
            damage_sound.Play();
        // actualizamos hud
        EventManager.instance.UpdateLifeEvent.Invoke(health);
    }

}
