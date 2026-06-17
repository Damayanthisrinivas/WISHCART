package com.example.data

// --- Product Data Models ---

data class Product(
    val id: String,
    val title: String,
    val category: String, // "Fashion", "Accessories", "Lifestyle", "Electronics", "Home & Living", "Sports", "Footwear", "Jewelry"
    val subcategory: String, // "Men", "Women", "Kids", "Watches", "Bags", "Sunglasses", "Beauty", "Mobiles", "Gadgets", "Personal Care"
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val imageUrls: List<String>, // We can use product images or gradient fallback images
    val rating: Double,
    val reviewsCount: Int,
    val description: String,
    val specifications: Map<String, String>,
    val sizes: List<String>,
    val colors: List<String>,
    val brand: String,
    val deliveryDays: Int = 3
)

object ProductCatalog {
    val categories = listOf(
        "Fashion",
        "Accessories",
        "Lifestyle",
        "Electronics",
        "Home & Living",
        "Sports",
        "Footwear",
        "Jewelry"
    )

    val brands = listOf(
        "Roadster", "Levis", "Zara", "Nike", "Adidas", "Casio", "Titan", "Boat", "OnePlus", "Samsung", "Loreal"
    )

    val allProducts = listOf(
        Product(
            id = "p1",
            title = "Premium Men's Indigo Slim Fit Cotton Shirt",
            category = "Fashion",
            subcategory = "Men",
            price = 899.0,
            originalPrice = 1499.0,
            discountPercent = 40,
            imageUrls = listOf("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500"),
            rating = 4.2,
            reviewsCount = 145,
            description = "Crafted with 100% premium breathable cotton, this slim fit indigo shirt offers comfortable all-day wear with an elegant mandarin collar, buttoned cuffs, and curated style. Perfect for both casual dinners and smart office days.",
            specifications = mapOf(
                "Fit" to "Slim Fit",
                "Fabric" to "100% Cotton",
                "Sleeve" to "Full Sleeves",
                "Pattern" to "Solid Indigo",
                "Occasion" to "Semi-Formal"
            ),
            sizes = listOf("S", "M", "L", "XL"),
            colors = listOf("Indigo Blue", "Navy Blue", "Pure White"),
            brand = "Roadster"
        ),
        Product(
            id = "p2",
            title = "Elegant Women's Floral Summer A-Line Dress",
            category = "Fashion",
            subcategory = "Women",
            price = 1299.0,
            originalPrice = 2599.0,
            discountPercent = 50,
            imageUrls = listOf("https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=500"),
            rating = 4.5,
            reviewsCount = 312,
            description = "Express your vibrancy with this stunning floral chiffon A-line dress, featuring a delicate sweetheart neckline, flexible cinched waist, and subtle pleated ruffles. Crafted with light silk-blend lining for breezy summer days.",
            specifications = mapOf(
                "Fabric" to "Chiffon Blend",
                "Length" to "Midi Length",
                "Pattern" to "Floral Printed",
                "Neckline" to "Sweetheart",
                "Occasion" to "Casual Holiday"
            ),
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf("Coral Peach", "Lemon Yellow", "Soft Lavender"),
            brand = "Zara"
        ),
        Product(
            id = "p3",
            title = "Active Smart Watch Classic with AMOLED Shield",
            category = "Accessories",
            subcategory = "Watches",
            price = 4999.0,
            originalPrice = 9999.0,
            discountPercent = 50,
            imageUrls = listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500"),
            rating = 4.6,
            reviewsCount = 1240,
            description = "The ultimate health and activity tracking companion. Equipped with a high-contrast dynamic AMOLED screen, real-time stress & oxygen analytics, built-in GPS mapping, and up to 14 days of robust power delivery.",
            specifications = mapOf(
                "Display" to "1.43\" AMOLED Display",
                "Waterproof" to "IP68 standard",
                "Strap Material" to "Premium Liquid Silicone",
                "Sensors" to "Heart-Rate, SpO2, Sleep Tracking"
            ),
            sizes = listOf("Universal"),
            colors = listOf("Charcoal Black", "Ocean Blue", "Cloud Gray"),
            brand = "Boat"
        ),
        Product(
            id = "p4",
            title = "Eco-Conscious Vintage Leather Handbag",
            category = "Accessories",
            subcategory = "Bags",
            price = 2499.0,
            originalPrice = 4999.0,
            discountPercent = 50,
            imageUrls = listOf("https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=500"),
            rating = 4.3,
            reviewsCount = 88,
            description = "Elegantly constructed utilizing vegetable-tanned full grain leather. Features spacious suede-lined dual compartments, zippered coin pockets, and high-tensile brass buckle sliders. Handcrafted with precision.",
            specifications = mapOf(
                "Material" to "Full Grain Vegetable-Tanned Leather",
                "Compartments" to "2 Main, 3 Inner Pockets",
                "Dimensions" to "12\" x 9.5\" x 4.5\"",
                "Handcrafted" to "Yes"
            ),
            sizes = listOf("Medium"),
            colors = listOf("Tan Brown", "Classic Black", "Burgundy Cherry"),
            brand = "Zara"
        ),
        Product(
            id = "p5",
            title = "Retro Polarized Aviator Sunglasses",
            category = "Accessories",
            subcategory = "Sunglasses",
            price = 1199.0,
            originalPrice = 1999.0,
            discountPercent = 40,
            imageUrls = listOf("https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=500"),
            rating = 4.1,
            reviewsCount = 74,
            description = "Shield your eyes in sheer timeless elegance. High-fidelity glass polarized lenses offer 100% UV400 suppression with advanced glare resistance, nestled in a strong ultra-lightweight stainless steel frame.",
            specifications = mapOf(
                "Lens Type" to "Polarized glass UV400",
                "Frame Material" to "Ultra-lightweight Stainless Steel",
                "Bridge width" to "14 mm",
                "Lens Width" to "58 mm"
            ),
            sizes = listOf("Standard"),
            colors = listOf("Dark Charcoal", "Golden Amber", "Silver Midnight"),
            brand = "Titan"
        ),
        Product(
            id = "p6",
            title = "Pro Run Hyper-Breathe Sports Footwear",
            category = "Footwear",
            subcategory = "Men",
            price = 3499.0,
            originalPrice = 6999.0,
            discountPercent = 50,
            imageUrls = listOf("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500"),
            rating = 4.7,
            reviewsCount = 598,
            description = "Experience lightning propulsion. Features specialized dual-density cloud cushions, breathable mesh weaves keeping your feet sweat-free, and high-elastic traction treads to support quick athletic maneuvers.",
            specifications = mapOf(
                "Sole" to "Dual-density React Cushion EVA Rubber",
                "Upper Material" to "Fly-Knit Mesh",
                "Activity" to "Running / Cross-Training",
                "Weight" to "220 grams"
            ),
            sizes = listOf("UK 7", "UK 8", "UK 9", "UK 10"),
            colors = listOf("Blaze Red", "Electric Blue", "Stealth Onyx"),
            brand = "Nike"
        ),
        Product(
            id = "p7",
            title = "Active-Buds Wireless Noise Cancelling Earbuds",
            category = "Electronics",
            subcategory = "Gadgets",
            price = 2999.0,
            originalPrice = 5999.0,
            discountPercent = 50,
            imageUrls = listOf("https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500"),
            rating = 4.5,
            reviewsCount = 490,
            description = "Immerse yourself completely in rich studio acoustics. Dual-speaker setups deliver robust bass response, while Active Hybrid Noise Cancellation suppresses up to 35dB of external ambient rumble.",
            specifications = mapOf(
                "ANC Depth" to "35dB Hybrid ANC",
                "Driver Unit" to "11mm Dynamic Drivers",
                "Battery" to "36 Hours Total Playback",
                "Bluetooth" to "v5.3 Low Energy"
            ),
            sizes = listOf("Universal"),
            colors = listOf("Ice White", "Cosmic Slate", "Teal Green"),
            brand = "Boat"
        ),
        Product(
            id = "p8",
            title = "Vibrant Kids Cotton Summer Play-Wear Set",
            category = "Fashion",
            subcategory = "Kids",
            price = 799.0,
            originalPrice = 1599.0,
            discountPercent = 50,
            imageUrls = listOf("https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=500"),
            rating = 4.4,
            reviewsCount = 82,
            description = "Adorable and comfortable play-wear combo containing a printed round-neck cotton tee and breathable elasticized drawstring shorts. Chemically untreated natural dyes offer gentle touch for sensitive baby skin.",
            specifications = mapOf(
                "Material" to "100% Organic Cotton",
                "Items Included" to "1 Tee-Shirt, 1 Shorts",
                "Wash Care" to "Gentle Machine Wash",
                "Age Group" to "2 - 6 Years"
            ),
            sizes = listOf("2-3Y", "4-5Y", "5-6Y"),
            colors = listOf("Sunny Yellow & Denim", "Sky Blue & Navy", "Mint Green & Gray"),
            brand = "Roadster"
        ),
        Product(
            id = "p9",
            title = "Premium Golden-Plated Emerald Dangle Jewelry Set",
            category = "Jewelry",
            subcategory = "Women",
            price = 1899.0,
            originalPrice = 3799.0,
            discountPercent = 50,
            imageUrls = listOf("https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=500"),
            rating = 4.6,
            reviewsCount = 61,
            description = "Dazzle during grand celebrations. Features synthetic emerald crystals precision cut to reflect light majestically, surrounding high-quality 18K gold polished brass filigree work. Set includes matching drop earrings.",
            specifications = mapOf(
                "Plating" to "18K Yellow Gold Polishing",
                "Gemstone" to "Hydrothermal Emerald & Faceted CZ",
                "Base Metal" to "Skin-Safe Brass Alloy",
                "Clasp Type" to "Sturdy Lobster Claw"
            ),
            sizes = listOf("Adjustable"),
            colors = listOf("Emerald Green & Gold", "Crimson Ruby & Gold", "Sapphire Blue & Silver"),
            brand = "Zara"
        ),
        Product(
            id = "p10",
            title = "All-Weather Microfiber Cotton Premium Duvet Set",
            category = "Home & Living",
            subcategory = "Home",
            price = 2199.0,
            originalPrice = 4399.0,
            discountPercent = 50,
            imageUrls = listOf("https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=500"),
            rating = 4.2,
            reviewsCount = 104,
            description = "Indulge in five-star sleep luxury. This double bedding setup contains 1 cozy down-alternative filled duvet, 1 premium flat sheet, and 2 breathable pillow cases crafted with 400 thread count ultra-fine microfiber.",
            specifications = mapOf(
                "Thread Count" to "400 TC",
                "Fabric" to "Hygroscopic Microfiber",
                "Size" to "Double Bed (King Size)",
                "Duvet Weight" to "2.2 Kgs"
            ),
            sizes = listOf("Queen", "King"),
            colors = listOf("Ivory Cream", "Slate Gray", "Sage Olive"),
            brand = "Casio"
        )
    )
}
