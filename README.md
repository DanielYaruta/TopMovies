# TopMovies

Android-приложение для просмотра фильмов с высоким рейтингом на основе [TMDB](https://www.themoviedb.org/).

## Возможности

- Список топ фильмов с бесконечной прокруткой и пагинацией
- Экран деталей: жанры, продолжительность, режиссёр, актёры, похожие фильмы
- Анимация общего элемента при переходе между экранами
- Скелетон-экран загрузки, pull-to-refresh, обработка ошибок
- **Поиск по названию** в реальном времени через Room (LIKE-запрос)
- **Кэш Room DB** — список загружается из базы данных без сети; pull-to-refresh сбрасывает кэш и тянет свежие данные
- **Экран приветствия** показывается при каждом третьем запуске (счётчик в SharedPreferences)
- Архитектура MVVM: ViewModel + StateFlow + Coroutines

## Технологии

| Слой | Технология |
|------|-----------|
| Язык | Kotlin |
| Архитектура | MVVM + Repository |
| Асинхронность | Coroutines + StateFlow |
| Сеть | Retrofit 2 + Gson |
| Локальная БД | Room 2.8.4 + KSP 2.2.10 |
| Изображения | Glide |
| UI | View Binding, RecyclerView, DiffUtil |
| Хранилище | SharedPreferences |
| Мин. версия | SDK 24 (Android 7.0) |

## Настройка

1. Получите бесплатный API-ключ на [themoviedb.org](https://www.themoviedb.org/settings/api)
2. Добавьте его в файл `local.properties`:
   ```
   tmdb.api_key=ВАШ_КЛЮЧ
   ```
3. Соберите и запустите проект в Android Studio

## Скриншоты

| Список | Поиск | Детали | Приветствие |
|--------|-------|--------|-------------|
| ![Список](screenshots/main.png) | ![Поиск](screenshots/search.png) | ![Детали](screenshots/detail.png) | ![Приветствие](screenshots/intro_collage.png) |
