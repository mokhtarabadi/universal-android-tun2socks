cmake_minimum_required(VERSION 3.10.2)
project("native-lib")

# log-lib
find_library(log-lib
        log)

# tun2socks-bridge
add_library(native-lib
        SHARED
        native-lib.cpp)

target_include_directories(native-lib PRIVATE
        ${CMAKE_SOURCE_DIR}/prebuilt/include)

target_link_libraries(native-lib
        ${CMAKE_SOURCE_DIR}/prebuilt/lib/${ANDROID_ABI}/libtun2socks.a
        ${log-lib})