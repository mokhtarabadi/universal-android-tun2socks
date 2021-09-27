#include <jni.h>
#include <string>
#include <cstdlib>
#include <pthread.h>
#include <unistd.h>
#include <android/log.h>
#include "tun2socks.h"

// Start threads to redirect stdout and stderr to logcat.
int pipe_stdout[2];
int pipe_stderr[2];
pthread_t thread_stdout;
pthread_t thread_stderr;
const char *ADBTAG = "tun2socks_bridge";

void *thread_stderr_func(void *) {
    ssize_t redirect_size;
    char buf[2048];
    while ((redirect_size = read(pipe_stderr[0], buf, sizeof buf - 1)) > 0) {
        //__android_log will add a new line anyway.
        if (buf[redirect_size - 1] == '\n') {
            --redirect_size;
        }
        buf[redirect_size] = 0;
        __android_log_write(ANDROID_LOG_ERROR, ADBTAG, buf);
    }
    return 0;
}

void *thread_stdout_func(void *) {
    ssize_t redirect_size;
    char buf[2048];
    while ((redirect_size = read(pipe_stdout[0], buf, sizeof buf - 1)) > 0) {
        //__android_log will add a new line anyway.
        if (buf[redirect_size - 1] == '\n') {
            --redirect_size;
        }
        buf[redirect_size] = 0;
        __android_log_write(ANDROID_LOG_INFO, ADBTAG, buf);
    }
    return 0;
}

int start_redirecting_stdout_stderr() {
    //set stdout as unbuffered.
    setvbuf(stdout, 0, _IONBF, 0);
    pipe(pipe_stdout);
    dup2(pipe_stdout[1], STDOUT_FILENO);

    //set stderr as unbuffered.
    setvbuf(stderr, 0, _IONBF, 0);
    pipe(pipe_stderr);
    dup2(pipe_stderr[1], STDERR_FILENO);

    if (pthread_create(&thread_stdout, 0, thread_stdout_func, 0) == -1) {
        return -1;
    }
    pthread_detach(thread_stdout);

    if (pthread_create(&thread_stderr, 0, thread_stderr_func, 0) == -1) {
        return -1;
    }
    pthread_detach(thread_stderr);

    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_mokhtarabadi_tun2socks_library_Tun2SocksBridge_printHelp(JNIEnv *env, jclass clazz) {
    print_help("badvpn-tun2socks");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_mokhtarabadi_tun2socks_library_Tun2SocksBridge_printVersion(JNIEnv *env, jclass clazz) {
    print_version();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_mokhtarabadi_tun2socks_library_Tun2SocksBridge__1native_1start(JNIEnv *env, jclass clazz,
                                                                        jobjectArray args) {

    //argc
    jsize argument_count = env->GetArrayLength(args);

    //Compute byte size need for all arguments in contiguous memory.
    int c_arguments_size = 0;
    for (int i = 0; i < argument_count; i++) {
        c_arguments_size += strlen(
                env->GetStringUTFChars((jstring) env->GetObjectArrayElement(args, i), 0));
        c_arguments_size++; // for '\0'
    }

    //Stores arguments in contiguous memory.
    char *args_buffer = (char *) calloc(c_arguments_size, sizeof(char));

    //argv to pass into badvpn-tun2socks.
    char *argv[argument_count];

    //To iterate through the expected start position of each argument in args_buffer.
    char *current_args_position = args_buffer;

    //Populate the args_buffer and argv.
    for (int i = 0; i < argument_count; i++) {
        const char *current_argument = env->GetStringUTFChars(
                (jstring) env->GetObjectArrayElement(args, i), 0);

        //Copy current argument to its expected position in args_buffer
        strncpy(current_args_position, current_argument, strlen(current_argument));

        //Save current argument start position in argv
        argv[i] = current_args_position;

        //Increment to the next argument's expected position.
        current_args_position += strlen(current_args_position) + 1;
    }

    //Start threads to show stdout and stderr in logcat.
    if (start_redirecting_stdout_stderr() == -1) {
        __android_log_write(ANDROID_LOG_ERROR, ADBTAG,
                            "Couldn't start redirecting stdout and stderr to logcat.");
    }

    //Start badvpn-tun2socks, with argc and argv.
    int result = start_tun2socks(argument_count, argv);
    free(args_buffer);

    return jint(result);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_mokhtarabadi_tun2socks_library_Tun2SocksBridge_terminate(JNIEnv *env, jclass clazz) {
    terminate();
}