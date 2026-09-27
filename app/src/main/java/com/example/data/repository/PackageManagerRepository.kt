package com.example.data.repository

import com.example.data.model.PackageInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PackageManagerRepository {

    private val initialPackages = listOf(
        PackageInfo(
            name = "requests",
            description = "HTTP library for Python, built for human beings.",
            version = "2.31.0",
            isInstalled = true,
            isPopular = true,
            author = "Kenneth Reitz",
            summaryAr = "مكتبة التعامل مع بروتوكول HTTP والشبكات"
        ),
        PackageInfo(
            name = "numpy",
            description = "Fundamental package for scientific computing with Python.",
            version = "1.26.4",
            isInstalled = false,
            isPopular = true,
            author = "NumPy Developers",
            summaryAr = "الحوسبة العلمية والمصفوفات الرياضية"
        ),
        PackageInfo(
            name = "pandas",
            description = "Powerful data structures for data analysis, time series, and statistics.",
            version = "2.2.2",
            isInstalled = false,
            isPopular = true,
            author = "The PyData Development Team",
            summaryAr = "تحليل وهيكلة البيانات والجداول"
        ),
        PackageInfo(
            name = "flask",
            description = "A lightweight WSGI web application framework in Python.",
            version = "3.0.2",
            isInstalled = false,
            isPopular = true,
            author = "Armin Ronacher",
            summaryAr = "إطار عمل ويب خفيف وسريع"
        ),
        PackageInfo(
            name = "beautifulsoup4",
            description = "Screen-scraping library for HTML and XML parsing.",
            version = "4.12.3",
            isInstalled = false,
            isPopular = true,
            author = "Leonard Richardson",
            summaryAr = "استخراج ومعالجة بيانات صفحات الويب"
        ),
        PackageInfo(
            name = "matplotlib",
            description = "Comprehensive library for creating static, animated, and interactive visualizations.",
            version = "3.8.4",
            isInstalled = false,
            isPopular = true,
            author = "John D. Hunter",
            summaryAr = "إنشاء المخططات والرسوم البيانية"
        ),
        PackageInfo(
            name = "scikit-learn",
            description = "Machine Learning in Python, simple and efficient tools for predictive data analysis.",
            version = "1.4.2",
            isInstalled = false,
            isPopular = true,
            author = "scikit-learn developers",
            summaryAr = "خوارزميات تعلم الآلة والذكاء الاصطناعي"
        ),
        PackageInfo(
            name = "pytest",
            description = "The pytest framework makes it easy to write small, readable tests.",
            version = "8.1.1",
            isInstalled = false,
            isPopular = false,
            author = "Holger Krekel",
            summaryAr = "أطر فحص واختبار الشيفرة البرمجية"
        ),
        PackageInfo(
            name = "pillow",
            description = "Python Imaging Library adds image processing capabilities.",
            version = "10.3.0",
            isInstalled = false,
            isPopular = false,
            author = "Alex Clark",
            summaryAr = "معالجة وتحرير الصور الرقمية"
        ),
        PackageInfo(
            name = "fastapi",
            description = "Modern, fast web framework for building APIs with Python 3.8+.",
            version = "0.110.1",
            isInstalled = false,
            isPopular = true,
            author = "Sebastián Ramírez",
            summaryAr = "بناء واجهات برمجية RESTful عالية الأداء"
        )
    )

    private val _packages = MutableStateFlow<List<PackageInfo>>(initialPackages)
    val packages: StateFlow<List<PackageInfo>> = _packages.asStateFlow()

    fun getInstalledPackages(): List<PackageInfo> {
        return _packages.value.filter { it.isInstalled }
    }

    fun installPackage(packageName: String) {
        val updated = _packages.value.map { pkg ->
            if (pkg.name.equals(packageName, ignoreCase = true)) {
                pkg.copy(isInstalled = true)
            } else {
                pkg
            }
        }.toMutableList()

        if (updated.none { it.name.equals(packageName, ignoreCase = true) }) {
            updated.add(
                PackageInfo(
                    name = packageName,
                    description = "Custom installed package via pip",
                    version = "1.0.0",
                    isInstalled = true,
                    isPopular = false,
                    summaryAr = "حزمة مخصصة مثبتة عبر pip"
                )
            )
        }
        _packages.value = updated
    }

    fun uninstallPackage(packageName: String) {
        _packages.value = _packages.value.map { pkg ->
            if (pkg.name.equals(packageName, ignoreCase = true)) {
                pkg.copy(isInstalled = false)
            } else {
                pkg
            }
        }
    }
}
