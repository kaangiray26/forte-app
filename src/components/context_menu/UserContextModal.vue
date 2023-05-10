<template>
    <div id="userContextOffcanvas" class="offcanvas offcanvas-bottom" tabindex="-1">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title">User Options</h5>
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="p-0 overflow-y-auto">
            <ul id="userMenu" class="list-group list-group-flush">
                <li v-show="!props.loved" class="list-group-item list-group-item-action" @click="action('addToFriends')">
                    <span class="bi bi-star me-1"></span>Add Friend
                </li>
                <li v-show="props.loved" class="list-group-item list-group-item-action"
                    @click="action('removeFromFriends')">
                    <span class="bi bi-star-fill me-1"></span>Remove Friend
                </li>
                <li class="list-group-item list-group-item-action" @click="action('openUserPage')">
                    <span class="bi bi-person-fill me-1"></span>User page
                </li>
                <li class="list-group-item list-group-item-action" @click="action('shareUser')">
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
    offcanvas = new Offcanvas(document.querySelector('#userContextOffcanvas'));
})
</script>