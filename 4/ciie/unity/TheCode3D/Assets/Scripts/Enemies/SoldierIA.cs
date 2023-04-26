using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;
using QuantumTek.QuantumTravel;


[RequireComponent(typeof(QT_MapObject))]
public class SoldierIA : MonoBehaviour {
    
    [SerializeField]
    public Animator anim;
    
    [Header("Enemy Settings")]
    [SerializeField]
    public float health = 100f;
    [SerializeField]
    public EnemyWeaponController weapon;
    [SerializeField]
    // radio de visión
    public float radius;
    [SerializeField]
    [Range(0,360)]
    // ángulo de visión
    public float angle;
    [SerializeField]
    public NavMeshAgent agent;
    [SerializeField]
    public Transform wayPointsObject;

    [Header("Masks")]
    public LayerMask playerMask;
    public LayerMask obstacleMask;

    private PlayerController player;
    private int currentWayPoint;
    private List<Transform> waypoints = new List<Transform>();
    private IEnumerator coroutine;
    private float distanceToTarget = Mathf.Infinity;
    private Vector3 directionToTarget = Vector3.zero;

    // flags
    private bool notifyed = false;
    private bool canSeePlayer = false;
    private bool shooting = false;
    private bool isDead = false;


    // Start is called before the first frame update
    void Start() {
        // el agente se inicializa quieto
        anim.SetInteger("Status_walk", 0);
        // obtenemos el player
        player = GameObject.FindGameObjectWithTag("Player").GetComponent<PlayerController>();
        // inicializamos el campo de visión de nuestro agente
        StartCoroutine(FieldOfViewRoutine());
        // le añadimos la ruta que le corresponde
        AddRoute();
    }

    // Update is called once per frame
    void Update() {
        if (isDead)
            return;
        if (!notifyed) {
            EventManager.instance.NewSoldierEvent.Invoke(gameObject.GetComponent<QT_MapObject>());
            notifyed = true;
        }
        // si ha detectado al jugador, le disparamos
        if (canSeePlayer) {
            // delay para evitar disparar en el momento que se ve al jugador
            if (!shooting)
                StartCoroutine(ShootDelay());
            else
                weapon.Shoot(distanceToTarget);
        } else {
            // si estaba disparando al jugador, pero ya no lo ve, le persigue
            if (shooting) {
                anim.SetInteger("Status_walk", 2);
                shooting = false;
            } else if (anim.GetInteger("Status_walk") == 1) {
                // si no lo hemos visto, ni se le ha disparado, sigue la ruta
                if (!isDead) {
                    agent.speed = 3.5f;
                    StartCoroutine(FollowRouteRoutine());
                }
            }
        }
    }

    // Corrutina para evitar disparar en el momento que se detecta al jugador
    // y hacer la IA más fácil de eliminar
    private IEnumerator ShootDelay() {
        yield return new WaitForSeconds(0.5f);
        // El agente pasa a modo ataque
        anim.SetInteger("Status_walk", 3);
        shooting = true;
        weapon.Shoot(distanceToTarget);
    }

    // Corrutina para ir al siguiente punto de la ruta
    private IEnumerator FollowRouteRoutine() {
        WaitForSeconds wait = new WaitForSeconds(0.5f);
        while (!isDead) {
            yield return wait;
            // En caso de haber acabado la ruta, la reinciamos
            if (currentWayPoint == waypoints.Count -1) {
                currentWayPoint = 0;
                // Estamos cinco segundos parados y reinciniamos (ya lo maneja el estado Idle)
                anim.SetInteger("Status_walk", 0);
                break;
            } else {
                FollowRoute();
            }
        }
    }

    // Seguir la ruta
    private void FollowRoute() {
        if (isDead)
            return;
        // Si el agente ha llegado a un punto, vamos al siguiente
        if (agent.remainingDistance <= agent.stoppingDistance) {
            currentWayPoint++;
            agent.SetDestination(waypoints[currentWayPoint].position);
        }
    }

    // Añadir la ruta correspondiente al soldado
    private void AddRoute() {
        foreach (Transform t in wayPointsObject)
            waypoints.Add(t);
        agent = anim.GetComponent<NavMeshAgent>();
        agent.SetDestination(waypoints[currentWayPoint].position);
    }

    // Función para recibir daño
    public void TakeDamage(float damage) {
        // si nos han matado, morimos
        if (health - damage <= 0) {
            health = 0;
            isDead = true;
            StartCoroutine(Die());
        } else {
            // restamos salud
            health -= damage;
            if (distanceToTarget < radius)
                // si el objetivo está dentro del radio de ataque del agente le ataca
                anim.SetInteger("Status_walk", 3);
            else 
                // si no, va a por el jugador
                anim.SetInteger("Status_walk", 2);
        }
    }

    // Corrutina para hacer que el agente muera
    private IEnumerator Die() {
        yield return null;
        EventManager.instance.DeadSoldierEvent.Invoke(gameObject.GetComponent<QT_MapObject>());
        Destroy(gameObject);
    }

    // Corrutina para detectar al jugador
    private IEnumerator FieldOfViewRoutine() {
        WaitForSeconds wait = new WaitForSeconds(0.2f);
        // Cada 0.2 segundos comprueba que el jugador esté dentro del campo de visión
        while (true) {
            yield return wait;
            FieldOfViewCheck();
        }
    }

    // Comprueba que el jugador esté dentro del campo de visión
    private void FieldOfViewCheck() {
        // realiza un sphere cast para detectar los colliders dentro de la máscara
        Collider[] rangeChecks = Physics.OverlapSphere(transform.position, radius, playerMask);
        if (rangeChecks.Length != 0) {
            // como dentro de la máscara del jugador solo está el jugador,
            // en cuanto detecte una colisión encontró al jugador
            Transform target = rangeChecks[0].transform;
            // calcula la dirección y la distancia del jugador
            directionToTarget = (target.position - transform.position).normalized;
            distanceToTarget = Vector3.Distance(transform.position, target.position);
            // si el jugador está dentro del ángulo de visión, va a por él o si el jugador se ha acercado por detrás
            // lo suficiente para que el agente lo detecte (la mitad del radio de visión delantero), trazamos un
            // raycast desde el agente hasta el jugador
            if ((Vector3.Angle(transform.forward, directionToTarget) < angle / 2) || distanceToTarget < radius/2) {
                // el raycast se realiza en la máscara de obstáculos y jugador, por lo que si el jugador está escondido detrás
                // de un objeto, el agente no lo verá, porque el raycast impacta en el obstáculo
                if (!Physics.Raycast(transform.position, directionToTarget, distanceToTarget, obstacleMask))
                    canSeePlayer = true;
                else {
                    canSeePlayer = false;
                    // si el jugador se acerca lo suficiente (un tercio del radio), y aunque esté escondido,
                    // el agente se dará cuenta de donde está y lo empezará a perseguir
                    if (distanceToTarget < radius/3)
                        anim.SetInteger("Status_walk", 2);
                }
            } else
                canSeePlayer = false;
        } else if (canSeePlayer)
            canSeePlayer = false;
    }
}
