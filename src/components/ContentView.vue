<template>
    <Login />
    <ContextMenu />
    <router-view />
</template>

<script setup>
import Login from './Login.vue';
import ContextMenu from './ContextMenu.vue';
</script>