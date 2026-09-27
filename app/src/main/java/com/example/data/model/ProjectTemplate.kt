package com.example.data.model

data class ProjectTemplate(
    val id: String,
    val titleKey: String,
    val descKey: String,
    val type: ProjectType,
    val iconName: String,
    val defaultFiles: Map<String, String>
)

object DefaultTemplates {
    val list = listOf(
        ProjectTemplate(
            id = "empty",
            titleKey = "tpl_empty_title",
            descKey = "tpl_empty_desc",
            type = ProjectType.STANDARD,
            iconName = "python",
            defaultFiles = mapOf(
                "main.py" to """# برنامج بسيط #
name = input("ما اسمك؟ ")
print(f"مرحباً {name}")

for i in range(5):
    print(f"الرقم : {i}")
""",
                "README.md" to "# MyProject\n\nA modern Python project created with Python IDE."
            )
        ),
        ProjectTemplate(
            id = "webapp",
            titleKey = "tpl_webapp_title",
            descKey = "tpl_webapp_desc",
            type = ProjectType.WEB_APP,
            iconName = "web",
            defaultFiles = mapOf(
                "app.py" to """from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/')
def home():
    return jsonify({"message": "Hello from Python IDE WebApp!", "status": "running"})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
""",
                "requirements.txt" to "flask>=3.0.0\nrequests>=2.31.0",
                "README.md" to "# WebApp\n\nFlask microservice application."
            )
        ),
        ProjectTemplate(
            id = "datascience",
            titleKey = "tpl_datascience_title",
            descKey = "tpl_datascience_desc",
            type = ProjectType.DATA_SCIENCE,
            iconName = "data",
            defaultFiles = mapOf(
                "analysis.py" to """import numpy as np

# توليد بيانات تجريبية
data = np.random.normal(loc=50, scale=10, size=100)
print(f"Mean: {np.mean(data):.2f}")
print(f"Standard Deviation: {np.std(data):.2f}")
print("Data processing finished successfully.")
""",
                "requirements.txt" to "numpy>=1.26.0\npandas>=2.2.0\nmatplotlib>=3.8.0",
                "README.md" to "# Data Science Project\n\nData analysis and visualization workspace."
            )
        ),
        ProjectTemplate(
            id = "ml",
            titleKey = "tpl_ml_title",
            descKey = "tpl_ml_desc",
            type = ProjectType.AI_TOOLS,
            iconName = "ai",
            defaultFiles = mapOf(
                "model.py" to """# نموذج تعلم الآلة
import numpy as np

class LinearModel:
    def __init__(self):
        self.w = 0.5
        self.b = 1.0

    def predict(self, x):
        return self.w * x + self.b

model = LinearModel()
print("Model initialized!")
for x in [1, 2, 3, 4, 5]:
    print(f"x={x} -> pred={model.predict(x)}")
""",
                "requirements.txt" to "scikit-learn>=1.4.0\ntorch>=2.2.0",
                "README.md" to "# Machine Learning\n\nAI and Machine Learning experimentation."
            )
        ),
        ProjectTemplate(
            id = "game",
            titleKey = "tpl_game_title",
            descKey = "tpl_game_desc",
            type = ProjectType.GAME,
            iconName = "game",
            defaultFiles = mapOf(
                "game.py" to """# لعبة بايثون 
import time

print("=== مرحباً بك في لعبة المغامرة ===")
player_hp = 100
print(f"طاقة اللاعب: {player_hp}")
print("بدأت المغامرة بنجاح!")
""",
                "requirements.txt" to "pygame-ce>=2.4.0",
                "README.md" to "# Python Game\n\nSimple interactive Python game."
            )
        ),
        ProjectTemplate(
            id = "android",
            titleKey = "tpl_android_title",
            descKey = "tpl_android_desc",
            type = ProjectType.MOBILE_APP,
            iconName = "android",
            defaultFiles = mapOf(
                "main.py" to """# تطبيق Kivy للأندرويد
print("Kivy Android App Template")
print("Initializing mobile UI framework...")
""",
                "buildozer.spec" to "[app]\ntitle = My Kivy App\npackage.name = mykivyapp",
                "requirements.txt" to "kivy>=2.3.0",
                "README.md" to "# Kivy Mobile App\n\nNative Python mobile application."
            )
        )
    )
}
