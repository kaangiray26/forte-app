<template>
    <div class="player-card hide-on-desktop">
        <ul class="nav nav-pills nav-fill">
            <li class="nav-item">
                <router-link to="/" class="nav-link text-white fw-bold" aria-current="page">
                    <span class="fs-5 bi me-1"
                        :class="{ 'bi-house-door-fill': path == '/', 'bi-house-door': path != '/' }"></span>
                </router-link>
            </li>
            <li class="nav-item">
                <router-link to="/search" class="nav-link text-white fw-bold" aria-current="page">
                    <span class="fs-5 bi me-1"
                        :class="{ 'bi bi-compass-fill': path.startsWith('/search'), 'bi bi-compass': !path.startsWith('/search') }"></span>
                </router-link>
            </li>
            <li class="nav-item">
                <router-link to="/profile" class="nav-link text-white fw-bold" aria-current="page">
                    <span class="fs-5 bi bi-person-circle me-1"></span>
                </router-link>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router'

const router = useRouter();

const path = computed(() => {
    return router.currentRoute.value.path;
})
</script>