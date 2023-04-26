using UnityEngine;
using UnityEngine.SceneManagement;

public class SceneTransicioner : MonoBehaviour{
    
    public void GoMainMenu(){
        SceneManager.LoadScene("MainMenu");
    }

    public void GoLevel(string level){
        SceneManager.LoadScene(level);
    }

    public void RestartCurrentScene(){
        int scene = SceneManager.GetActiveScene().buildIndex;
        SceneManager.LoadScene(scene, LoadSceneMode.Single);
    }

    public void Exit(){
        Application.Quit();
    }
}
