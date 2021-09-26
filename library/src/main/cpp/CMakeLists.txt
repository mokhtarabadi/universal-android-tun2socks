cmake_minimum_required(VERSION 3.10.2)

project("tun2socks-bridge")

include_directories(prebuilt)

# badvpn static lib
add_library(badvpn
        STATIC
        IMPORTED)
set_target_properties(
        badvpn
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn.a)

# tun2socks-bridge
add_library(tun2socks-bridge
        SHARED
        tun2socks-bridge.cpp)

# log-lib
find_library(log-lib
        log)

target_link_libraries(
        tun2socks-bridge
        badvpn
        ${log-lib})