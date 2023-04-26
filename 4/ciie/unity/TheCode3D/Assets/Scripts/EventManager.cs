using System;
using UnityEngine;
using UnityEngine.Events;
using QuantumTek.QuantumTravel;

[Serializable]
public class Int3Event : UnityEvent<int, int, int> { }
public class FloatEvent : UnityEvent<float> { }
public class SoldierEvent : UnityEvent<QT_MapObject> { }

public class EventManager : MonoBehaviour{
    #region Singleton

    public static EventManager instance;
    private void Awake(){
        if (instance == null){
            instance = this;
        }
        else if (instance != this){
            Destroy(gameObject);
        }
    }

    #endregion

    public Int3Event UpdateBulletsEvent = new Int3Event();

    public UnityEvent DialogEndEvent = new UnityEvent();

    public FloatEvent UpdateLifeEvent = new FloatEvent();

    public SoldierEvent NewSoldierEvent = new SoldierEvent();

    public SoldierEvent DeadSoldierEvent = new SoldierEvent();

}
