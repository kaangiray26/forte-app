<template>
    <div v-show="!loaded">
        <div class="d-flex justify-content-center m-3">
            <button class="btn btn-dark" type="button" disabled>
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                Loading...
            </button>
        </div>
    </div>
    <div class="card rounded-0 border-0" v-show="loaded">
        <div class="card-body rounded p-4">
            <div class="row g-3">
                <div class="col-12">
                    <div class="d-flex position-relative">
                        <div class="ph-friend-full">
                            <img class="img-square playlist-img rounded" :src="get_cover()" @error="placeholder" />
                        </div>
                        <div class="position-absolute bottom-0 right-0">
                            <button class="btn btn-light action-btn bi bi-pencil-square m-2" type="button"
                                @click="change_cover">
                            </button>
                        </div>
                        <input ref="cover_upload" type="file" class="visually-hidden" @change="handle_cover" />
                    </div>
                </div>
                <div class="col">
                    <h1 class="theme-color album-title mb-4">{{ profile.username }}</h1>
                    <hr />
                    <router-view />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import { store } from "/js/store.js";

const profile = ref({});
const cover_upload = ref(null);
const loaded = ref(false);

async function placeholder(obj) {
    obj.target.remove();
}

function get_cover() {
    if (profile.value.cover) {
        return ft.server + '/' + profile.value.cover;
    }

    return "/images/friend.svg";
}

async function get_profile() {
    let data = await ft.API('/profile');
    if (!data) return;

    profile.value = data.profile;
    loaded.value = true;
}

async function handle_cover() {
    let file = cover_upload.value.files[0];

    if (!file || !file.type.startsWith('image/')) {
        return;
    }

    let formData = new FormData();
    formData.append("cover", file);
    let response = await ft.upload_cover(formData);

    if (response.hasOwnProperty('cover')) {
        profile.value.cover = response.cover;
    }
}

async function change_cover() {
    cover_upload.value.click();
}

onBeforeMount(() => {
    if (store.offline) return;
    get_profile();
})
</script>