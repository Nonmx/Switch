package sselab.switchy.api;

public enum API {
	ActivateTask, TerminateTask, ChainTask, // Task state controlling API
											// functions
	Schedule, GetResource, ReleaseResource, SetEvent, WaitEvent, ClearEvent, SetRelAlarm, CancelAlarm,
	
	/*
	 * APIs of Ubinos OS
	 * */
	
	task_create, task_sleep, //task related
	
	//mutex related
	mutex_create,mutex_delete,mutex_lock, mutex_unlock, mutex_lock_timed,mutex_is_locked,
	
	//semaphore related
	sem_create, sem_delete,sem_take,sem_give,
	
	//message Q related
	msgq_create, msgq_receive, msgq_send;
	
	

	/**
	 * Returns API Kind by name.
	 * @param name
	 * @return API
	 */
	public static API getAPIByName(String name) {
		switch (name) {
		case "ActivateTask":
			return ActivateTask;
		case "TerminateTask":
			return TerminateTask;
		case "ChainTask":
			return ChainTask;
		case "Schedule":
			return Schedule;
		case "GetResource":
			return GetResource;
		case "ReleaseResource":
			return ReleaseResource;
		case "SetEvent":
			return SetEvent;
		case "WaitEvent":
			return WaitEvent;
		case "ClearEvent":
			return ClearEvent;
		case "SetRelAlarm":
			return SetRelAlarm;
		case "CancelAlarm":
			return CancelAlarm;
		case "task_create":
			return task_create;
		case "task_sleep":
			return task_sleep;
		case "mutex_create":
			return mutex_create;
		case "mutex_delete":
			return mutex_delete;
		case "mutex_lock":
			return mutex_lock;
		case "mutex_unlock":
			return mutex_unlock;
		case"mutex_lock_timed":
			return mutex_lock_timed;
		case "mutex_is_locked":
			return mutex_is_locked;
		case "sem_create":
			return sem_create;
		case "sem_delete":
			return sem_delete;
		case "sem_take":
			return sem_take;
		case "sem_give":
			return sem_give;
		case "msgq_create":
			return msgq_create;
		case "msgq_receive":
			return msgq_receive;
		case "msgq_send":
			return msgq_send;
		default:
			return null;
			
		}
	}
}
