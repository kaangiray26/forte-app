<template>
    <div class="card h-100 w-100 border-0">
        <div class="p-0">
            <div class="position-relative rounded">
                <div class="ph rounded" @click="open_downloads">
                    <img class="img-square img-fluid placeholder-img rounded" src="images/tricks.jpg">
                </div>
                <div class="position-absolute bottom-0 right-0 m-2">
                    <button class="btn btn-light action-btn bi bi-play-fill" type="button" @click="open_downloads">
                    </button>
                </div>
            </div>
            <div class="d-flex flex-fill">
                <h6 class="theme-color  fw-bold text-break text-wrap clickable p-2 ps-0" @click="open_downloads">You are
                    offline.</h6>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

async function open_downloads() {
    router.push("/offline");
}
</script>