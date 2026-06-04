package com.example.unit_coverter.core.cooking

import java.math.BigDecimal

// Densities in g/mL — USDA/culinary references, 3 significant figures.
private fun d(s: String) = BigDecimal(s)

val INGREDIENTS: List<Ingredient> = listOf(
    // ── Liquids ──────────────────────────────────────────────────────────────
    Ingredient("water",         "Water",                d("1.000"), listOf("water")),
    Ingredient("milk_whole",    "Milk (whole)",         d("1.030"), listOf("milk", "whole milk")),
    Ingredient("milk_skim",     "Milk (skim)",          d("1.036"), listOf("skim milk", "nonfat milk")),
    Ingredient("heavy_cream",   "Heavy Cream",          d("1.005"), listOf("heavy cream", "double cream", "whipping cream")),
    Ingredient("buttermilk",    "Buttermilk",           d("1.033"), listOf("buttermilk")),
    Ingredient("sour_cream",    "Sour Cream",           d("1.004"), listOf("sour cream")),
    Ingredient("vegetable_oil", "Vegetable Oil",        d("0.917"), listOf("vegetable oil", "canola oil", "sunflower oil")),
    Ingredient("olive_oil",     "Olive Oil",            d("0.914"), listOf("olive oil")),
    Ingredient("honey",         "Honey",                d("1.420"), listOf("honey")),
    Ingredient("maple_syrup",   "Maple Syrup",          d("1.320"), listOf("maple syrup")),
    Ingredient("yogurt",        "Yogurt (plain)",       d("1.058"), listOf("yogurt", "yoghurt")),

    // ── Fats / Dairy ─────────────────────────────────────────────────────────
    Ingredient("butter",        "Butter",               d("0.959"), listOf("butter")),
    Ingredient("peanut_butter", "Peanut Butter",        d("1.067"), listOf("peanut butter")),
    Ingredient("cream_cheese",  "Cream Cheese",         d("1.029"), listOf("cream cheese")),

    // ── Flours ───────────────────────────────────────────────────────────────
    Ingredient("flour_ap",      "Flour (all-purpose)",  d("0.528"), listOf("all purpose flour", "plain flour", "flour")),
    Ingredient("flour_bread",   "Flour (bread)",        d("0.536"), listOf("bread flour")),
    Ingredient("flour_cake",    "Flour (cake)",         d("0.500"), listOf("cake flour")),
    Ingredient("flour_ww",      "Flour (whole wheat)",  d("0.569"), listOf("whole wheat flour", "wholemeal flour")),
    Ingredient("almond_flour",  "Almond Flour",         d("0.400"), listOf("almond flour", "ground almonds")),
    Ingredient("cornstarch",    "Cornstarch",           d("0.541"), listOf("cornstarch", "corn flour", "cornflour")),
    Ingredient("cocoa_powder",  "Cocoa Powder",         d("0.480"), listOf("cocoa powder", "cocoa")),

    // ── Sugars ───────────────────────────────────────────────────────────────
    Ingredient("sugar_white",   "Sugar (white)",        d("0.845"), listOf("sugar", "white sugar", "granulated sugar", "caster sugar")),
    Ingredient("sugar_powdered","Powdered Sugar",        d("0.512"), listOf("powdered sugar", "icing sugar", "confectioners sugar")),
    Ingredient("sugar_brown",   "Brown Sugar (packed)", d("0.930"), listOf("brown sugar", "light brown sugar", "dark brown sugar")),

    // ── Grains / Dry goods ───────────────────────────────────────────────────
    Ingredient("rice_white",    "Rice (white, dry)",    d("0.845"), listOf("rice", "white rice", "long grain rice")),
    Ingredient("rice_brown",    "Rice (brown, dry)",    d("0.860"), listOf("brown rice")),
    Ingredient("oats_rolled",   "Rolled Oats",          d("0.380"), listOf("oats", "rolled oats", "porridge oats")),
    Ingredient("breadcrumbs",   "Breadcrumbs (dry)",    d("0.507"), listOf("breadcrumbs", "bread crumbs")),

    // ── Leavening / Salt ─────────────────────────────────────────────────────
    Ingredient("salt",          "Salt (fine)",          d("1.217"), listOf("salt", "table salt", "fine salt")),
    Ingredient("baking_soda",   "Baking Soda",          d("1.080"), listOf("baking soda", "bicarbonate of soda", "bicarb")),
    Ingredient("baking_powder", "Baking Powder",        d("0.812"), listOf("baking powder")),
)
