using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class EnemyWeaponController : MonoBehaviour {
    
    [Header ("Gun Settings")]
    [SerializeField]
    public float damage = 10f;

    [Header ("Weapon Animations")]
    [SerializeField]
    private ParticleSystem shootingSystem;
    [SerializeField]
    public AudioSource shootSound;
    [SerializeField]
    private Transform bulletSpawnPoint;
    [SerializeField]
    private ParticleSystem impactParticleSystem;
    [SerializeField]
    private TrailRenderer bulletTrail;
    
    [Header ("Shoot Settings")]
    [SerializeField]
    private float shootDelay = 0.5f;
    [SerializeField]
    private float bulletSpeed = 350f;
    
    [Header ("Bullet Spreading")]
    [SerializeField]
    private Vector3 bulletSpreadVariance = new Vector3(0.1f, 0.1f, 0.1f);
    
    [SerializeField]
    private LayerMask mask;
    
    private float lastShootTime;
    private Vector3 originPosition;
    private PlayerController player;


    private void Start(){
        originPosition = transform.localPosition;
        player = GameObject.FindGameObjectWithTag("Player").GetComponent<PlayerController>();
    }


    public void Shoot(float distanceToTarget) {
        // utilizamos la distancia al jugador para ajustar
        // la dispersión de los disparos
        float conversion = distanceToTarget / 50;
        if ((lastShootTime + shootDelay) < Time.time) {
            // Animacion de fogeo y sonido
            shootingSystem.Play();
            shootSound.Play();
            // Calculamos la dirección de disparo y la dispersión
            Vector3 direction = GetDirection(conversion);
            // Si el raycast impacta, el trail se renderiza hasta el punto de impacto, utilizamos las
            // capas de obstáculos y jugador para trazar este
            if (Physics.Raycast(bulletSpawnPoint.position, direction, out RaycastHit hit, float.MaxValue, mask)) {
                StartCoroutine(SpawnTrail(hit));
            } else {
                // Si no impacta, lo renderizamos desde la boquilla en línea recta + dispersión una determinada distancia
                StartCoroutine(SpawnTrail(direction));
            }
            lastShootTime = Time.time;
        }
    }
    
    // Aleatorizar el vector que indica la direccion de disparo (bullet spread)
    private Vector3 GetDirection(float distance) {
        Vector3 direction = player.transform.position - bulletSpawnPoint.position;
        // si la distancia es superior a 50 metros, no aplicamos dispersión
        if (distance < 1) {
            direction += new Vector3(
                Random.Range(-(bulletSpreadVariance.x/distance), (bulletSpreadVariance.x/distance)),
                Random.Range(-(bulletSpreadVariance.y/distance), (bulletSpreadVariance.y/distance)),
                Random.Range(-(bulletSpreadVariance.z/distance), (bulletSpreadVariance.z/distance))
            );
        }
        direction.Normalize();
        return direction;

    }

    // Animación de disparo hacia objetos dentro de las máscaras de impacto
    private IEnumerator SpawnTrail(RaycastHit hit) {
        float time = 0;
        // Creamos un objeto que animará nuestros disparos
        TrailRenderer trail = Instantiate(bulletTrail, bulletSpawnPoint.position, Quaternion.identity);
        Vector3 startPosition = trail.transform.position;
        float distance = hit.distance;
        // un impacto q este a BulletSpeed m -> 1 segundo
        // distancia/velocidad = tiempo
        // spawneamos la bala un determinado tiempo
        while (time < distance/bulletSpeed) {
            // movimiento de la bala
            trail.transform.position = Vector3.Lerp(startPosition, hit.point, time);
            time += bulletSpeed*Time.deltaTime/distance;
            yield return null;
        }
        trail.transform.position = hit.point;
        // después del tiempo necesario para llegar al punto de impacto
        // comprobamos si hemos impactado en el jugador
        PlayerController impact = hit.transform.GetComponent<PlayerController>();
        if (impact != null)
            // si impactamos en el jugador, le quitamos salud
            impact.TakeDamage(damage);
        else
            // si no, instanciamos un animación de impacto en un obstáculo
            Instantiate(impactParticleSystem, hit.point, Quaternion.LookRotation(hit.normal));
        Destroy(trail.gameObject, trail.time);
    }

    // Animación de disparo hacia una dirección, pero sin ningún impacto,
    // por ejemplo disparar al cielo
    private IEnumerator SpawnTrail(Vector3 direction) {
        float time = 0;
        TrailRenderer trail = Instantiate(bulletTrail, bulletSpawnPoint.position, Quaternion.identity);
        Vector3 startPosition = trail.transform.position;
        // precalculamos la distancia a la que llegará la bala
        Vector3 endPosition = startPosition + direction * 100;
        float distance = Vector3.Distance(startPosition, endPosition);
        // renderizamos el efecto un determinado tiempo y después lo destruimos
        while (time < distance/bulletSpeed) {
            trail.transform.position = Vector3.Lerp(startPosition, endPosition, time);
            time += bulletSpeed*Time.deltaTime/distance;
            yield return null;
        }
        trail.transform.position = endPosition;
        Destroy(trail.gameObject, trail.time);
    }
}
