SpaceCraft
==========

DoTo:
> Caching von Planetenbahnen
> Mehr Blöcke
> Schöneres Startmenü

Regeln:
* Alle Klassen und Methoden werden (auf Deutsch) genau beschrieben (Getter und Setter nicht zwingend)
* Variablennamen etc. auf Englisch
* Kommentare auf Deutsch, Englisch, FranzÃ¶sisch oder Japanisch (aber nur wenn es witzig ist!)
* Nur die Klassen importieren die wirklich benÃ¶tigt werden (nicht z.B.: import java.awt.*;)
* Ã„nderungen in Klassen von allgemeiner Relevanz mit Versionsdatum, Person und Beschreibung notieren!

History:
 __.__.2019 v0.0.1 AK  * Erster Test
 04.04.2019 v0.0.2 AK  * Erste Version
 05.04.2019 v0.0.3 AK  * vereinfachte Grafik fÃ¼r Craft geht/ geom erweitert/ key/mouse events
 06.04.2019 v0.0.4 LG  * Verbesserung der Grafik, BufferedImages
 09.04.2019 v0.0.5 LG  * VektorLGlump, OrbitGlump, SerializationGlump:
                         Es gibt 5 "Serialisierungs-BlÃ¶cke": die Liste der Player, der Space, die
                         Liste der Planeten, die Liste der Schiffe, die Liste der BlÃ¶cke
                         Das stimmt nicht mehr. (26.05.2019) -LG
 11.04.2019 v0.0.6 LG  * Timer
 12.04.2019 v0.1.0 LG  * ZusammenfÃ¼hrung mit v0.0.7_AK !Auf keinen Fall verwenden!
 13.04.2019 v0.1.1 AK  * Kleine Ã„nderungen und Kommentare fÃ¼r Linus (soweit ich es verstanden hab)
 18.04.2019 v0.1.2 LG  * Settings, verwirrende Space-Berechnungen, Korrektur an Vektor(D/L/I).subtract
 30.04.2019 v0.1.3 AK  * Suche des Fehlers in Craft; Mouse Events PlayerS Zeug (verschieben der Ansicht))
 30.04.2019 v0.1.4_AK  * PlayerS Ansicht; Ã„nderungen in Listener; geom Vektor.set(Vektor v); Settings 
                         CurrentPlanet im Player
 30.04.2019 v0.1.5_AK  * PlayerS Ansicht; focusMass fertig; Ã„nderungen in Settings und Shortcuts. Versuch
                         InfoPopups zu implementieren
 31.04.2019 v0.1.5_AK  * Ã„nderungen bei BlÃ¶cken. Versuch Pistons zu implementieren
 02.05.2019 v0.1.6_LG  * Bugfix beim StÃ¼rzen in einen Planeten (aber noch keine Impulsaddition!), ZusammenfÃ¼hrung mit v0.1.3_LG
 02.05.2019 v0.1.7_AK  * Matadaten added; SBlock added; left/rightClick events in Craft erneuert
 03.05.2019 v0.1.7_AK  * Blocks Framework fertig; Pistons implementiert; Ã„nderung von Metadata in Meta 
                         Framework fÃ¼r Menus und implementierung des Pause Menus. Versuch Note-Block
 03.05.2019 v0.1.8_AK  * NoteBlock geht
 05.05.2019 v0.1.9_AK  * Blocks id statisch; Blocks werden nicht mehr in den Gamesaves gespeichert; Attribute von BlÃ¶cken; 
                         Player Login; rewrite Ã¶ffnen und schlieÃŸen von Menus; PlayerS Grafik mit BufferedImage
 06.05.2019 v0.2.0_LG  * Server/Client-System. Server und Client kommunizieren nur Ã¼ber Tasks (Server->Client) und Requests(Client->Server)
                         Player mÃ¼ssen synchronisiert werden.
 06.05.2019 v0.2.1_LG  * Ich warte ... ~ AK
 06.05.2019 v0.2.2_LG  * "Leg' dich in die HÃ¤ngematte, nicht ins Zeug" ~ Linus bevor er die readme anpasst :)
 12.05.2019 v0.2.3_AK  * Merge mit v0.1.10_AK: Inventar Items Drop and Drag; moved ioTools to package util; moved geom to package util; 
 14.05.2019 v0.2.4_LG  * Umstieg auf Sockets (unvollstÃ¤ndig, Tasks fehlen noch und aus irgendeinem Grund 
                         kann man nur fÃ¼r ein Frame in die Space-Darstellung wechseln (ist auf irgendeine obskure Weise vom Default-Wert
                         fÃ¼r inCraft abhÃ¤ngig), Space-Zeichnen ist sehr langsam)
 15.05.2019 v0.2.5_LG  * Bugfix der Space-Zeichnung, Weiterentwicklung der Sockets
 15.05.2019 v0.2.6_LG  * Weiterentwicklung der Sockets
 16.05.2019 v0.2.7_LG  * Bugfix mit ReadResolve und Socket, static Main Main.main
 12.05.2019 v0.2.3_AK  * Merge mit v0.1.10_AK: Inventar Items Drop and Drag; moved ioTools to package util; moved geom to package util;
 13.05.2019 v0.2.4_LG  * Cache der Map im Player
 13.05.2019 v0.2.5_MH  * Merge v0.1.10_MH: StartMenu, LoginMenu; PlayerMenu; Menu geÃ¤ndert; in menu neue sachen hinzugefÃ¼gt
 15.05.2019 v0.2.6_AK  * Die Menus jetzt im Package client.menus; PaintPanel und OverLayPanels; PlayerTexture als JComponent; 
                         TextureSelectMenu; ImageTools.resize(); Lustige Wortspiele mit case und break
 17.05.2019 v0.3.0_LG  * Zusammenfassen von v0.2.6_AK mit v0.2.7_LG
 17.05.2019 v0.3.1_AK  * kleine Bugfixes (siehe Issues auf Github), BlÃ¶cke Client und Serverseitig, Auf Linus Fragen im Code geantwortet
 17.05.2109 v0.3.2_AK  * Breakment und placement_predictions; Break und Place clientside //nettes Datum -LG
 18.05.2019 v0.3.3_LG  * Merge mit v0.2.5_MH
 19.05.2019 v0.3.4_AK  * PlayerTexture geÃ¤ndert; HÃ¤ssliche LÃ¶sung fÃ¼r #26; OtherPlayerTextures; Name von Spielern; kleine Bugfixes (LG)
 20.05.2019 v0.3.5_LG  * SerialVersionUIDs: Wann immer etwas Serialisierungs-relevantes in einer Klasse gemacht werden, muss
                         das um 1 erhÃ¶ht werden.
 21.05.2019 v0.3.6_MH  * Schriftarten hinzugefÃ¼gt und damit die Menus verschÃ¶nert
 22.05.2019 v0.3.7_LG  * kleine Ã„nderungen: Items, massChanges (ungetestet)
                         Craft: FlieÃŸkommakoordinaten-Darstellung (noch nicht wirklich getestet)
                         Subsandbox-Zeichnen (noch ohne Interaktionen!)
 23.05.2019 v0.3.8.0_AK  * Chest; Ã„nderungen in Main
 23.05.2019 v0.3.8.1_BB * getPosToCache mit Double hinzugefügt und grobe Kollision hinzugefügt(wird verfeinert)
 24.05.2019 v0.3.9_LG  * Merge mit v0.3.8_LG/ ShipsC und PlanetCs wieder entfernt (=> nur noch masses)
 24.05.2019 v0.3.10_AK * Hotbar Anfang; Ã„nderungen im Inv und im PlayerC mouseEvent
 24.05.2019 v0.3.8.0_LG  * Arbeitsweltraum
 24.05.2019 v0.3.8.1_BB * Creative hinzugefügt Kollision ist feiner, getFoot
 26.05.2019 v0.3.9_LG  * ManÃ¶ver im Arbeitsweltraum, Anwenden dieser ManÃ¶ver dann im richtigen Weltraum
 26.05.2019 v0.3.11_LG * Merge mit v0.3.10_AK (eigentlich andersherum)
 27.05.2019 v0.3.12_LG * Noch ein Merge, warum auch immer
 28.05.2019 v0.3.13_MH * Chat und Data Panels, Chatcommands
 29.05.2019 v0.3.14_LG * Sehr unsichere PasswÃ¶rter, in Space Umstieg auf ManÃ¶ver (vorher: Orbit- und MassChanges)
 29.05.2019 v0.3.15_AK * ManoeuvreInfo Menu
 31.05.2019 v0.3.16.0_LG * Anwendung der Manos, unfertig (was noch fehlt: dMass, fuelcost,rel?), generell recht unschÃƒÂ²Ã‚Â®Ã¢â€šÂ¬Ã¢â€šÂ¬
 31.05.2019 v0.3.16.1_BB * timersetup Gravitiation (kollission nach unten ist wieder ungenau)
 01.06.2019 v0.3.17_AK * Das blÃ¶de Inventar drop and drag [sic]. Uhrzeit: 00:42; Laune: schlechter als Benni
 02.06.2019 v0.4.0_AK  * Offset Problem (#26) gelÃ¶st (Juhu); Inventar verbessert; DraggedItemImage hinzugefÃ¼gt
 02.06.2019 v0.4.1_AK  * Kleine Verbesserungen Drop and Drag; CraftingRecipes implementiert; DataPanel nicht mehr enabled
 02.06.2019 v0.4.2_LG  * GroÃŸe Teile von Space und Mass in den Client verlagert (vermeidet Redundanz)
 03.06.2019 v0.3.17_BB * kollission genau
 04.06.2019 v0.3.18_BB * Springen limitiert
 04.06.2019 v0.3.19_BB * mapDir, getSector(),getPlayerHitsDiagonal(),getNewMapDir() hinzugefügt
 04.06.2019 v0.4.3_MH  * Mapgen Ã¼berarbeitet und Erze hinzugefÃ¼gt
 04.06.2019 v0.4.4_LG  * schlechte Landung auf Planeten, ein bisschen Subsandbox-Zeug
 05.06.2019 v0.4.5_LG  * Abbauen und Platzieren in Subsandboxen
 06.06.2019 v0.4.6_LG  * Dokumentation
 07.06.2019 v0.5.0_AK  * Merge mit v0.4.6_AK (Inventar fertig) => abgegebene Version
 20.06.2019 v0.5.1.0_LG  * tatsÃ¤chlicher Raketenstart mÃ¶glich
 24.06.2019 v0.5.1.1_BB  * Merge Bug moveleft moveright
 28.06.2019 v0.5.1.2_BB  *Bug moveleft moveright behoben, collide hinzugefügt
 28.06.2019 v0.5.2_AK  * mehr BlÃ¶cke und Items (planks, sticks, glass, roof-tiles); Setzlinge und BÃ¤ume (mit LG); Doors;  Ã„nderungen in 
                         client.PlayerC. ; Craftingrezepte; neue Texturen (V, MH, AK)
 29.06.2019 v0.5.3_LG  * Startmenü jetzt mit Serverkontrolle, aber zugegebenermaßen sehr hässlich
 08.07.2019 v0.5.4_BB  * Eine unausgereifte Kollision und Gravitation (wie Linus... Alles muss ich (AK) machen :( ) 
 
 // Einige Versionen sind manuell ohne VersionsberÃ¼cksichtigung gemergt und deswegen nicht hier aufgelistet.
 // Bitte alle Ã„nderungen hier mit Versionsnummer, Person und Beschreibung notieren.
 
Hinweis:
 Falsch zugeordnete Zitate dienen nur der witzigkeit des Code und sind nicht Meinung des AutorX. Beim Schreiben dieses Satzes wurde keine Tastatur verletzt
 LG = Schnux = KÃ¤ptn ( = Linus)
 AK = unknown = Alex
 MH = maxx = Max
 BB = Benny = Benedikt
 FG = Floh
 Es werden Texturen vom default texture-pack von Minetest und von der Minetest Mod mesecons verwendet.
 
 Witziges:
 * java.lang.NoSuchMethodException
 * public String password   // existiert leider nicht mehr :(
 * Arbeitsweltraum
 * public static VektorI SCHEISS_OFFSET = new VektorI(7,32);
 
=======
Hi du Jemand. Kannst gerne einen Kommentar dalassen. Wird eh keiner lesen...  ~not_unknown
