using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class InGamePause : MonoBehaviour{

    public static bool gameInPause = false;
    public static bool win = false;
    public GameObject pauseMenu;
    [HideInInspector]
    private AudioManager audioManager; 

    void Start(){
        audioManager = AudioManager.instance;
    }
    
    void Update(){
        // Al pulsar ESC
        if (Input.GetKeyDown(KeyCode.Escape)){
            if (gameInPause)
                Resume();
            else
                if (Time.timeScale!=0f)
                    Pause();
        }
    }

    public void Resume(){
        Cursor.lockState = CursorLockMode.Locked;
        pauseMenu.SetActive(false);
        Time.timeScale = 1f;
        gameInPause = false;
        audioManager.Stop("Pause");
        if (!win)
            audioManager.Play("Theme");
        else
            audioManager.Play("Victory");
    }

    public void Pause(){
        Cursor.lockState = CursorLockMode.None;
        pauseMenu.SetActive(true);
        Time.timeScale = 0f;
        gameInPause = true;
        audioManager.Pause("Theme");
        if (audioManager.isPlaying("Victory")) {
            audioManager.Pause("Victory");
            win = true;
        }
        audioManager.Play("Pause");
    }

}
