using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;

public class SchutzPursuitBehavior : StateMachineBehaviour {
    
    private NavMeshAgent agent;
    private Transform player;
    
    // OnStateEnter is called when a transition starts and the state machine starts to evaluate this state
    override public void OnStateEnter(Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
        // obtenemos el NavMeshAgent del soldado
        agent = animator.GetComponent<NavMeshAgent>();
        // ponemos velocidad de correr
        agent.speed = 7.5f;
        // obtenemos el jugador
        player = GameObject.FindGameObjectWithTag("Player").transform;
    }

    // OnStateUpdate is called on each Update frame between OnStateEnter and OnStateExit callbacks
    override public void OnStateUpdate(Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
        // el destino es el jugador
        agent.SetDestination(player.position);
        float distance = Vector3.Distance(player.position, animator.transform.position);
        // cuando llegue a una determinada distancia, se para y cambia al estado de ataque
        if (distance < 50f)
            animator.SetInteger("Status_walk", 3);
    }

    // OnStateExit is called when a transition ends and the state machine finishes evaluating this state
    override public void OnStateExit(Animator animator, AnimatorStateInfo stateInfo, int layerIndex) {
        // la posición de destino a salida es la suya propia
        agent.SetDestination(agent.transform.position);
    }

    // OnStateMove is called right after Animator.OnAnimatorMove()
    //override public void OnStateMove(Animator animator, AnimatorStateInfo stateInfo, int layerIndex)
    //{
    //    // Implement code that processes and affects root motion
    //}

    // OnStateIK is called right after Animator.OnAnimatorIK()
    //override public void OnStateIK(Animator animator, AnimatorStateInfo stateInfo, int layerIndex)
    //{
    //    // Implement code that sets up animation IK (inverse kinematics)
    //}
}
