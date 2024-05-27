# das Wetter
A simple Android app for accessing the current weather and the forecast for the upcoming week. If you think it looks familiar, that may be because I took inspiration from the AccuWeather app that came preinstalled on my phone. Please don't sue me.

# WeatherAPI
This app is powered by [WeatherAPI.com](https://www.weatherapi.com/).

# My To Do List
1. swipe between multiple locations
2. sticky header and footer
3. why is the header bright white when i specifically made it blue in the themes xml files
4. convert PAQI from step function to linear

# Building
If you intent to build this app yourself, there are a few things you will need.
1. A free API key from the above link.
2. Weather icons. The above website includes some free weather icons that are used throughout the app. However, I opted against pulling every icon from the web every time the app updates. Instead, I included the icons in the res/drawable folder.
   1. Download the icons from [WeatherAPI/icons](https://www.weatherapi.com/docs/#weather-icons)
   2. Unzip
   3. I used a clunky little `for %f in (weather/day/*) do ren ".\weather\day\%f" "day%f"` and similar for night
   4. Put those icons into the drawable folder.
