using UnityEngine;
using UnityEngine.UI;
using TMPro;
using System.Collections.Generic;

public class OptionsController : MonoBehaviour{

    public Slider volumeSlider;
    public Toggle fsToggle;
    public TMP_Dropdown resDropdown;
    private Resolution[] resolutions;
    public TMP_Dropdown qualityDropdown;
    public int quality = 3;
    public Slider sensSlider;
    public GameObject playerController;

    public void Start(){ 
        // Volume
        volumeSlider.value = PlayerPrefs.GetFloat("volumeAudio", .5f);
        AudioListener.volume = volumeSlider.value;
        // FS
        fsToggle.isOn = Screen.fullScreen;
        // Resolution
        CheckResolution();
        // Quality
        qualityDropdown.value = PlayerPrefs.GetInt("quality", quality);
        ChangeQuality(qualityDropdown.value);
        // Sens
        sensSlider.value = PlayerPrefs.GetFloat("sensibility", .5f);
        ChangeSens(sensSlider.value);
    }


    //////////////////
    //    Volume    //
    //////////////////
    public void ChangeVolume(float value){
        PlayerPrefs.SetFloat("volumeAudio", value);
        AudioListener.volume = volumeSlider.value;
    }

    ///////////////////////
    //    Resolutions    //
    ///////////////////////

    private void CheckResolution(){
        // Loading Resolutions
        resolutions = Screen.resolutions;
        resDropdown.ClearOptions();
        List<string> stringResList = new List<string>();

        // Revisamos Valores De resolución actuales
        int actualRes = 0;
        bool foundActualRes = false;
        
        int prefRes = PlayerPrefs.GetInt("resolution", -1);
        if (prefRes!=-1){
            actualRes = prefRes;
            foundActualRes = true;
        } 
        
        foreach (Resolution res in resolutions) { 
            string strRes = res.width +  " x " + res.height;
            stringResList.Add(strRes);

            if (!foundActualRes)
                if (Screen.fullScreen && res.width == Screen.currentResolution.width && res.height == Screen.currentResolution.height)
                    foundActualRes = true;
                else
                    actualRes++;

        }

        // Adding to DropDown and Setting Actual Resolution
        resDropdown.AddOptions(stringResList);
        resDropdown.value = actualRes;
        resDropdown.RefreshShownValue();
        ChangeResolution(resDropdown.value);

    }

    public void ChangeResolution(int index){
        PlayerPrefs.SetInt("resolution", index);
        Resolution resolucion = resolutions[index];
        Screen.SetResolution(resolucion.width, resolucion.height, Screen.fullScreen);
    }

    public void ChangeFs(bool value){
        Screen.fullScreen = value;
    }

    ///////////////////
    //    Quality    //
    ///////////////////
    
    public void ChangeQuality(int index){
        PlayerPrefs.SetInt("quality", index);
        QualitySettings.SetQualityLevel(index);
    }

    ///////////////////
    //     Sens      //
    ///////////////////
    
    public void ChangeSens(float value){
        PlayerPrefs.SetFloat("sensibility", value);
        if (playerController != null)
            playerController.GetComponent<PlayerController>().rotationSensibility = 100f + value * 1000f;
    }

}

