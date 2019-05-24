SpaceCraft
==========

DoTo:
> Caching von Planetenbahnen
> Mehr Blöcke
> Space-Craft-Schnittstelle

Regeln:
* Alle Klassen und Methoden werden (auf Deutsch) genau beschrieben (Getter und Setter nicht zwingend)
* Variablennamen etc. auf Englisch
* Kommentare auf Deutsch, Englisch, Französisch oder Japanisch (aber nur wenn es witzig ist!)
* Nur die Klassen importieren die wirklich benötigt werden (nicht z.B.: import java.awt.*;)
* Änderungen in Klassen von allgemeiner Relevanz mit Versionsdatum, Person und Beschreibung notieren!

History:
 __.__.2019 v0.0.1 AK * Erster Test
 04.04.2019 v0.0.2 AK * Erste Version
 05.04.2019 v0.0.3 AK * vereinfachte Grafik für Craft geht/ geom erweitert/ key/mouse events
 06.04.2019 v0.0.4 LG * Verbesserung der Grafik, BufferedImages
 09.04.2019 v0.0.5 LG * VektorLGlump, OrbitGlump, SerializationGlump:
                        Es gibt 5 "Serialisierungs-Blöcke": die Liste der Player, der Space, die
                        Liste der Planeten, die Liste der Schiffe, die Liste der Blöcke
 11.04.2019 v0.0.6 LG * Timer
 12.04.2019 v0.1.0 LG * Zusammenführung mit v0.0.7_AK !Auf keinen Fall verwenden!
 13.04.2019 v0.1.1 AK * Kleine Änderungen und Kommentare für Linus (soweit ich es verstanden hab)
 18.04.2019 v0.1.2 LG * Settings, verwirrende Space-Berechnungen, Korrektur an Vektor(D/L/I).subtract
 30.04.2019 v0.1.3 AK * Suche des Fehlers in Craft; Mouse Events PlayerS Zeug (verschieben der Ansicht))
 30.04.2019 v0.1.4_AK * PlayerS Ansicht; Änderungen in Listener; geom Vektor.set(Vektor v); Settings 
                        CurrentPlanet im Player
 30.04.2019 v0.1.5_AK * PlayerS Ansicht; focusMass fertig; Änderungen in Settings und Shortcuts. Versuch
                        InfoPopups zu implementieren
 31.04.2019 v0.1.5_AK * Änderungen bei Blöcken. Versuch Pistons zu implementieren
 02.05.2019 v0.1.6_LG * Bugfix beim Stürzen in einen Planeten (aber noch keine Impulsaddition!), Zusammenführung mit v0.1.3_LG
 02.05.2019 v0.1.7_AK * Matadaten added; SBlock added; left/rightClick events in Craft erneuert
 03.05.2019 v0.1.7_AK * Blocks Framework fertig; Pistons implementiert; Änderung von Metadata in Meta 
                        Framework für Menus und implementierung des Pause Menus. Versuch Note-Block
 03.05.2019 v0.1.8_AK * NoteBlock geht
 05.05.2019 v0.1.9_AK * Blocks id statisch; Blocks werden nicht mehr in den Gamesaves gespeichert; Attribute von Blöcken; 
                        Player Login; rewrite öffnen und schließen von Menus; PlayerS Grafik mit BufferedImage
 06.05.2019 v0.2.0_LG * Server/Client-System. Server und Client kommunizieren nur über Tasks (Server->Client) und Requests(Client->Server)
                        Player müssen synchronisiert werden.
 06.05.2019 v0.2.1_LG * Ich warte ... ~ AK
 06.05.2019 v0.2.2_LG * "Leg' dich in die Hängematte, nicht ins Zeug" ~ Linus bevor er die readme anpasst :)
 12.05.2019 v0.2.3_AK * Merge mit v0.1.10_AK: Inventar Items Drop and Drag; moved ioTools to package util; moved geom to package util; 
 14.05.2019 v0.2.4_LG * Umstieg auf Sockets (unvollständig, Tasks fehlen noch und aus irgendeinem Grund 
                        kann man nur für ein Frame in die Space-Darstellung wechseln (ist auf irgendeine obskure Weise vom Default-Wert
                        für inCraft abhängig), Space-Zeichnen ist sehr langsam)
 15.05.2019 v0.2.5_LG * Bugfix der Space-Zeichnung, Weiterentwicklung der Sockets
 15.05.2019 v0.2.6_LG * Weiterentwicklung der Sockets
 16.05.2019 v0.2.7_LG * Bugfix mit ReadResolve und Socket, static Main Main.main
 
 12.05.2019 v0.2.3_AK * Merge mit v0.1.10_AK: Inventar Items Drop and Drag; moved ioTools to package util; moved geom to package util;
 13.05.2019 v0.2.4_LG * Cache der Map im Player
 13.05.2019 v0.2.5_MH * Merge v0.1.10_MH: StartMenu, LoginMenu; PlayerMenu; Menu geändert; in menu neue sachen hinzugefügt
 15.05.2019 v0.2.6_AK * Die Menus jetzt im Package client.menus; PaintPanel und OverLayPanels; PlayerTexture als JComponent; 
                        TextureSelectMenu; ImageTools.resize(); Lustige Wortspiele mit case und break
 17.05.2019 v0.3.0_LG * Zusammenfassen von v0.2.6_AK mit v0.2.7_LG
 17.05.2019 v0.3.1_AK * kleine Bugfixes (siehe Issues auf Github), Blöcke Client und Serverseitig, Auf Linus Fragen im Code geantwortet
 17.05.2109 v0.3.2_AK * Breakment und placement_predictions; Break und Place clientside //nettes Datum -LG
 18.05.2019 v0.3.3_LG * Merge mit v0.2.5_MH
 19.05.2019 v0.3.4_AK * PlayerTexture geändert; Hässliche Lösung für #26; OtherPlayerTextures; Name von Spielern; kleine Bugfixes (LG)
 20.05.2019 v0.3.5_LG * SerialVersionUIDs: Wann immer etwas Serialisierungs-relevantes in einer Klasse gemacht werden, muss
                        das um 1 erhöht werden.
 21.05.2019 v0.3.6_MH * Schriftarten hinzugefügt und damit die Menus verschönert
 22.05.2019 v0.3.7_LG * kleine Änderungen: Items, massChanges (ungetestet)
                        Craft: Fließkommakoordinaten-Darstellung (noch nicht wirklich getestet)
                        Subsandbox-Zeichnen (noch ohne Interaktionen!)
 23.05.2019 v0.3.8_AK * Chest; Änderungen in Main
 24.05.2019 v0.3.9_LG * Merge mit v0.3.8_LG/ ShipsC und PlanetCs wieder entfernt (=> nur noch masses)
 24.05.2019 v0.3.10_AK* Hotbar Ánfang; Änderungen im Inv und im PlayerC mouseEvent
                        
 // Bitte alle Änderungen hier mit Versionsnummer, Person und Beschreibung notieren.
 
Hinweis:
 Falsch zugeordnete Zitate dienen nur der witzigkeit des Code und sind nicht Meinung des AutorX. Beim Schreiben dieses Satzes wurde keine Tastatur verletzt
 LG = Schnux = Käptn ( = Linus)
 AK = unknown = Alex
 MH = maxx = Max
 BB = Benny = Benedikt
 FG = Floh
 
 Witziges:
 * java.lang.NoSuchMethodError