<template>
    <div id="albumContextOffcanvas" class="offcanvas offcanvas-bottom" tabindex="-1">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title">Album Options</h5>
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="p-0 overflow-y-auto">
            <ul id="albumMenu" class="list-group list-group-flush">
                <li class="list-group-item list-group-item-action" @click="action('playAlbum')">
                    <span class="bi bi-play-fill me-1"></span>Play
                </li>
                <li class="list-group-item list-group-item-action" @click="action('playAlbumNext')">
                    <span class="bi bi-fast-forward-fill me-1"></span>Play next
                </li>
                <li v-show="!props.loved" class="list-group-item list-group-item-action" @click="action('addToLoved')">
                    <span class="bi bi-star me-1"></span>Like
                </li>
                <li v-show="props.loved" class="list-group-item list-group-item-action" @click="action('removeFromLoved')">
                    <span class="bi bi-star-fill me-1"></span>Unlike
                </li>
                <li class="list-group-item list-group-item-action" @click="action('addAlbumToQueue')">
                    <span class="bi bi-music-note-list me-1"></span>Add to queue
                </li>
                <li class="list-group-item list-group-item-action" @click="action('openAlbumPage')">
                    <span class="bi bi-vinyl-fill me-1"></span>Album page
                </li>
                <li class="list-group-item list-group-item-action" @click="action('openArtistPage')">
                    <span class="bi bi-person-fill me-1"></span>Artist page
                </li>
                <li class="list-group-item list-group-item-action" @click="action('shareAlbum')">
                    <span class="bi bi-share-fill me-1"></span>Share
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { Offcanvas } from 'bootstrap';

let offcanvas = null;
const emit = defineEmits(['context-menu-event']);

const props = defineProps({
    loved: {
        type: Boolean,
        required: true
    }
})

async function _show() {
    offcanvas.show();
}

async function _hide() {
    offcanvas.hide();
}

async function action(event) {
    offcanvas.hide();
    emit('context-menu-event', event);
}

defineExpose({
    show: _show,
    hide: _hide
})

onMounted(() => {
    offcanvas = new Offcanvas(document.querySelector('#albumContextOffcanvas'));
})
</script>