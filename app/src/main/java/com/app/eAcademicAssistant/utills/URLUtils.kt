package com.app.eAcademicAssistant.utills


object URLUtils {

    private const val PORT_API = 2222


    private const val URL_ROOT = "http://teamayka.com"

    private const val URL_BASE = "$URL_ROOT:$PORT_API"



    const val URL_USER_LOGIN= "$URL_BASE/user_login"
    const val URL_USER_REGISTER= "$URL_BASE/user_register"
    const val URL_FORGET_PASSWORD= "$URL_BASE/forget_password"


    const val URL_DEPARTMENT_CREATE= "$URL_BASE/department_create"
    const val URL_COURSE_CREATE= "$URL_BASE/course_create"
    const val URL_SEM_CREATE= "$URL_BASE/sem_create"
    const val URL_SUBJECT_CREATE= "$URL_BASE/subject_create"


    const val URL_COURSE_EDIT= "$URL_BASE/course_edit"
    const val URL_SUBJECT_EDIT= "$URL_BASE/subject_edit"
    const val URL_SEM_EDIT= "$URL_BASE/sem_edit"
    const val URL_DEPARTMENT_EDIT= "$URL_BASE/department_edit"
    const val URL_USER_EDIT= "$URL_BASE/user_edit"
    const val URL_SEM_DAY_LINKING_EDIT= "$URL_BASE/sem_day_linking_edit"




    const val URL_DEPARTMENT_LIST= "$URL_BASE/department_list"
    const val URL_SEM_LIST= "$URL_BASE/sem_list"
    const val URL_USER_LIST= "$URL_BASE/user_list"
    const val URL_COURSE_LIST= "$URL_BASE/course_list"
    const val URL_SUBJECT_LIST= "$URL_BASE/subject_list"
    const val URL_GET_STUDENT_LIST= "$URL_BASE/get_student_list"
    const val URL_SEM_COURSE_LIST= "$URL_BASE/sem_course_list"
    const val URL_SEM_SUBJECT_LIST= "$URL_BASE/sem_subject_list"
    const val URL_COURSE_SEM_STUDENT_LIST= "$URL_BASE/course_sem_student_list"
    const val URL_TEACHERS_DUTY_LINKING_LIST= "$URL_BASE/teachers_duty_linking_list"
    const val URL_COURSE_DEPARTMENT_LINKING_LIST= "$URL_BASE/course_department_linking_list"
    const val URL_SEM_DAY_LINKING_LIST= "$URL_BASE/sem_day_linking_list"
    const val URL_SEM_DAY_LINKING_LIST_BY_TIME= "$URL_BASE/sem_day_linking_list_by_time"
    const val URL_STUDENT_ATTENDANCE_LIST= "$URL_BASE/student_attendance_list"



    const val URL_SEM_COURSE_LINKING= "$URL_BASE/sem_course_linking"
    const val URL_SEM_SUBJECT_ID_LINKING= "$URL_BASE/sem_subject_id_linking"
    const val URL_STUDENT_SEM_COURSE_LINKING= "$URL_BASE/student_sem_course_linking"
    const val URL_TEACHER_DUTY_LINKING= "$URL_BASE/teacher_duty_linking"
    const val URL_SEM_DAY_LINKING= "$URL_BASE/sem_day_linking"



    const val URL_SEM_COURSE_LINKING_STATUS_CHANGE= "$URL_BASE/sem_course_linking_status_change"
    const val URL_DEPARTMENT_STATUS_CHANGE= "$URL_BASE/department_status_change"
    const val URL_SEMESTER_STATUS_CHANGE= "$URL_BASE/semester_status_change"
    const val URL_COURSE_STATUS_CHANGE= "$URL_BASE/course_status_change"
    const val URL_SUBJECT_STATUS_CHANGE= "$URL_BASE/subject_status_change"
    const val URL_SEMESTER_SUBJECT_LINKING_STATUS_CHANGE= "$URL_BASE/semester_subject_linking_status_change"
    const val URL_USERS_STATUS_CHANGE= "$URL_BASE/users_status_change"
    const val URL_STUDENT_SEM_COURSE_LINKING_STATUS_CHANGE= "$URL_BASE/student_sem_course_linking_status_change"
    const val URL_DAY_PERIOD_LINKING= "$URL_BASE/day_period_linking"



    const val URL_STUDENT_ATTENDANCE_ADD= "$URL_BASE/student_attendance_add"


}

