<template></template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { App } from '@capacitor/app';

const router = useRouter();
const paths = ["/", "/search", "/profile", "/artists", "/albums", "/playlists"];

async function navigation() {
    router.back();
}

const visible = computed(() => {
    let path = router.currentRoute.value.path;
    if (path.startsWith('/search')) return false;
    if (path.startsWith('/profile')) return false;
    if (path.startsWith('/user')) return false;
    return !paths.includes(router.currentRoute.value.path);
})

App.addListener('backButton', () => {
    if (visible.value) navigation();
})
</script>