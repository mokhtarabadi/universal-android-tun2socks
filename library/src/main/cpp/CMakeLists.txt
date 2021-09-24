cmake_minimum_required(VERSION 3.10.2)

project("tun2socks-bridge")

include_directories(prebuilt)

# base
add_library(badvpn-base
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-base
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-base.so)

# flow
add_library(badvpn-flow
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-flow
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-flow.so)

# flowextra
add_library(badvpn-flowextra
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-flowextra
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-flowextra.so)

# lwip
add_library(badvpn-lwip
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-lwip
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-lwip.so)

# socks_udp_client
add_library(badvpn-socks_udp_client
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-socks_udp_client
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-socks_udp_client.so)

# socksclient
add_library(badvpn-socksclient
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-socksclient
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-socksclient.so)

# system
add_library(badvpn-system
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-system
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-system.so)

# tun2socks
add_library(badvpn-tun2socks
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-tun2socks
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-tun2socks.so)

# tuntap
add_library(badvpn-tuntap
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-tuntap
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-tuntap.so)

# udpgw_client
add_library(badvpn-udpgw_client
        SHARED
        IMPORTED)
set_target_properties(
        badvpn-udpgw_client
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/prebuilt/${ANDROID_ABI}/libbadvpn-udpgw_client.so)

# tun2socks-bridge
add_library(tun2socks-bridge
        SHARED
        tun2socks-bridge.cpp)

# log-lib
find_library(log-lib
        log)

target_link_libraries(
        tun2socks-bridge
        badvpn-base
        badvpn-flow
        badvpn-flowextra
        badvpn-lwip
        badvpn-socks_udp_client
        badvpn-socksclient
        badvpn-system
        badvpn-tun2socks
        badvpn-tuntap
        badvpn-udpgw_client
        ${log-lib})