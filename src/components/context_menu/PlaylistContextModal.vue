<template>
    <div id="playlistContextOffcanvas" class="offcanvas offcanvas-bottom" tabindex="-1">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title">Playlist Options</h5>
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div ref="scroll" class="p-0 overflow-y-auto">
            <ul id="playlistMenu" class="list-group list-group-flush" @click="action('playPlaylist')">
                <li class="list-group-item list-group-item-action">
                    <span class="bi bi-play-fill me-1"></span>Play
                </li>
                <li class="list-group-item list-group-item-action" @click="action('playPlaylistNext')">
                    <span class="bi bi-fast-forward-fill me-1"></span>Play next
                </li>
                <li v-show="!props.loved" class="list-group-item list-group-item-action" @click="action('addToLoved')">
                    <span class="bi bi-star me-1"></span>Like
                </li>
                <li v-show="props.loved" class="list-group-item list-group-item-action" @click="action('removeFromLoved')">
                    <span class="bi bi-star-fill me-1"></span>Unlike
                </li>
                <li class="list-group-item list-group-item-action" @click="action('addPlaylistToQueue')">
                    <span class="bi bi-music-note-list me-1"></span>Add to queue
                </li>
                <li class="list-group-item list-group-item-action" @click="action('openPlaylistPage')">
                    <span class="bi bi-music-note-beamed me-1"></span>Playlist page
                </li>
                <li class="list-group-item list-group-item-action" @click="action('openAuthorPage')">
                    <span class="bi bi-person-fill me-1"></span>Author page
                </li>
                <li class="list-group-item list-group-item-action" @click="action('sharePlaylist')">
                    <span class="bi bi-share-fill me-1"></span>Share
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
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
    scroll.value.scrollTop = 0;
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
    offcanvas = new Offcanvas(document.querySelector('#playlistContextOffcanvas'));
})
</script>