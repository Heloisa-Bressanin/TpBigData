<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-950">
    <header class="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 px-6 py-4">
      <div class="max-w-7xl mx-auto flex items-center justify-between">
        <div class="flex items-center gap-3">
          <UIcon name="i-heroicons-truck" class="text-primary-500 w-7 h-7" />
          <span class="text-xl font-bold text-gray-900 dark:text-white">Suivi Livraisons</span>
        </div>
        <nav class="flex items-center gap-2">
          <UButton variant="ghost" to="/" icon="i-heroicons-home">Dashboard</UButton>
          <UButton variant="ghost" to="/livraisons" icon="i-heroicons-list-bullet">Livraisons</UButton>
          <UButton variant="ghost" to="/nouvelle" icon="i-heroicons-plus-circle">Nouvelle</UButton>
          <UButton variant="ghost" to="/stats" icon="i-heroicons-chart-bar">Stats</UButton>
        </nav>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-6 py-8">
      <slot />
    </main>
  </div>
</template>
