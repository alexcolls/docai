<template>
  <div class="min-h-screen">
    <LayoutHeader />
    <UContainer class="h-fit">
      <CommonBgVideoYoutube>
        <slot />
      </CommonBgVideoYoutube>
    </UContainer>
    <LayoutFooter />
  </div>
</template>
