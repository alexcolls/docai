<template>
  <div
    class="relative w-full flex mb-4 rounded-full hover:border-primary/20
      border border-transparent cursor-pointer"
    @click="moveMenuToCursor"
  >
    <ModalUploadDoc />
    <div
      ref="glassMenu"
      class="flex justify-between p-2 pr-3 w-fit space-x-2 sm:space-x-4
        rounded-full shadow-md border border-black/5 bg-black/10
        backdrop-blur-lg dark:border-white/10 dark:bg-white/15 !z-[9999]
        cursor-grab active:cursor-grabbing transition-transform duration-300
        ease-out select-none"
      @mousedown="startDrag"
      @touchstart="startDrag"
    >
      <div class="flex flex-wrap pl-4 py-2">
        <HomeChatMenuSelectEngine />
        <HomeChatMenuSelectDocType />
      </div>
      <HomeChatMenuBtnUpload class="mt-4 sm:mt-0" />
    </div>
  </div>
</template>

<script setup lang="ts">

const { app } = useStore();

watch(() => app.selectedDocTypeId, (v) => {
  if (v) {
    updateSizes();
  }
});

const rightMargin = 2;
const glassMenu = ref<HTMLDivElement>();
const isDragging = ref(false);
const startX = ref(0);
const dragStartX = ref(0);
const currentX = ref(0);
const parentWidth = ref(0);
const menuWidth = ref(0);
let lastX = 0;
let lastEventTime = 0;
let velocity = 0;

const updateSizes = () => {
  nextTick(() => {
    const parent = glassMenu.value?.parentElement as HTMLElement | null;
    if (parent && glassMenu.value) {
      parentWidth.value = parent.clientWidth;
      menuWidth.value = glassMenu.value.clientWidth;
      currentX.value = Math.max(0, Math.min(
        app.chatMenuPosition, parentWidth.value - menuWidth.value - rightMargin
      ));
      applyTranslation(currentX.value);
    }
  });
};

onMounted(() => {
  updateSizes();
  window.addEventListener('resize', updateSizes);
});

onUnmounted(() => {
  window.removeEventListener('resize', updateSizes);
});

const startDrag = (event: MouseEvent | TouchEvent) => {
  isDragging.value = true;
  const clientX = 'clientX' in event
    ? event.clientX
    : event.touches[0]?.clientX || 0;
  startX.value = clientX;
  dragStartX.value = currentX.value;
  lastX = clientX;
  lastEventTime = performance.now();
  const moveHandler = (event: MouseEvent | TouchEvent) => {
    if (!isDragging.value || !glassMenu.value) { return; }
    const clientX = 'clientX' in event
      ? event.clientX
      : event.touches[0]?.clientX || 0;
    const deltaX = clientX - startX.value;
    let newX = dragStartX.value + deltaX;
    newX = Math.max(0,
      Math.min(parentWidth.value - menuWidth.value - rightMargin, newX));
    const now = performance.now();
    const dt = now - lastEventTime;
    if (dt > 0) {
      velocity = (clientX - lastX) / dt;
      lastX = clientX;
      lastEventTime = now;
    }
    applyTranslation(newX);
  };
  const stopHandler = () => {
    isDragging.value = false;
    const momentumDistance = velocity * 200;
    let momentumX = currentX.value + momentumDistance;
    momentumX = Math.max(0,
      Math.min(parentWidth.value - menuWidth.value - rightMargin, momentumX));
    if (glassMenu.value) {
      glassMenu.value.style.transition = 'transform 0.4s ease-out';
      applyTranslation(momentumX);
      setTimeout(() => {
        if (glassMenu.value) {
          glassMenu.value.style.transition = '';
        }
      }, 400);
    }
    window.removeEventListener('mousemove', moveHandler);
    window.removeEventListener('mouseup', stopHandler);
    window.removeEventListener('touchmove', moveHandler);
    window.removeEventListener('touchend', stopHandler);
  };
  window.addEventListener('mousemove', moveHandler);
  window.addEventListener('mouseup', stopHandler);
  window.addEventListener('touchmove', moveHandler);
  window.addEventListener('touchend', stopHandler);
};

const applyTranslation = (x: number) => {
  if (glassMenu.value) {
    glassMenu.value.style.transform = `translateX(${x}px)`;
    currentX.value = x;
    app.chatMenuPosition = x;
  }
};

const moveMenuToCursor = (event: MouseEvent) => {
  if (!glassMenu.value) { return; }
  const parent = glassMenu.value.parentElement as HTMLElement | null;
  if (!parent) { return; }
  const clientX = event.clientX;
  const parentRect = parent.getBoundingClientRect();
  const newX = Math.max(0, Math.min(
    clientX - parentRect.left - menuWidth.value / 2,
    parentWidth.value - menuWidth.value - rightMargin
  ));
  glassMenu.value.style.transition = 'transform 0.3s ease-out';
  applyTranslation(newX);
  setTimeout(() => {
    if (glassMenu.value) {
      glassMenu.value.style.transition = '';
    }
  }, 300);
};

</script>
