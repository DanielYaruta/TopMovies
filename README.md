# TopMovies

Android-приложение для просмотра фильмов с высоким рейтингом на основе [TMDB](https://www.themoviedb.org/).

## Возможности

- Список топ фильмов с бесконечной прокруткой и пагинацией
- Экран деталей: жанры, продолжительность, режиссёр, актёры, похожие фильмы
- Анимация общего элемента при переходе между экранами
- Скелетон-экран загрузки, pull-to-refresh, обработка ошибок
- Архитектура MVVM: ViewModel + StateFlow + Coroutines

## Технологии

| Слой | Технология |
|------|-----------|
| Язык | Kotlin |
| Архитектура | MVVM + Repository |
| Асинхронность | Coroutines + StateFlow |
| Сеть | Retrofit 2 + Gson |
| Изображения | Glide |
| UI | View Binding, RecyclerView, DiffUtil |
| Мин. версия | SDK 24 (Android 7.0) |

## Настройка

1. Получите бесплатный API-ключ на [themoviedb.org](https://www.themoviedb.org/settings/api)
2. Добавьте его в файл `local.properties`:
   ```
   tmdb.api_key=ВАШ_КЛЮЧ
   ```
3. Соберите и запустите проект в Android Studio

## Скриншоты

| Список | Детали |
|--------|--------|
| ![Список](screenshots/main.png) | ![Детали](screenshots/detail.png) |
