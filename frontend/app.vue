<template>
  <div
    class="transition-all duration-500 ease-in-out bg-gray-200
      dark:bg-gray-800 relative overflow-hidden min-h-screen"
  >
    <CommonCursorLight
      :color="ui.primary"
      :turn-light="ui.flashlight"
    />
    <div class="transition-colors duration-300 ease-in-out">
      <NuxtLayout>
        <UNotifications
          :ui="{
            position: 'bottom-12'
          }"
        />
        <NuxtPage />
      </NuxtLayout>
    </div>
  </div>
</template>

<script setup>

const { ui } = useStore();

</script>

<style>
body {
  font-family: 'Orbitron', sans-serif;
}
.page-enter-active,
.page-leave-active {
  transition: all 0.6s;
}
.page-enter-from,
.page-leave-to {
  opacity: 0;
}
</style>
