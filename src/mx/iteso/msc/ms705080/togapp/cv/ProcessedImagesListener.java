/*
 * Copyright 2017 Mario Contreras <marioc@nazul.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.iteso.msc.ms705080.togapp.cv;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public interface ProcessedImagesListener {

    public void imageUpdated(List<BufferedImage> images);
}

// EOF
