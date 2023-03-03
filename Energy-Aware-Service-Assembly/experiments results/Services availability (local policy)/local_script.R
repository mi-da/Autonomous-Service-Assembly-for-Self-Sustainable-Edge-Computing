# execute all lines: ctrl + shift + enter

library(ggplot2)

# set working directory here
setwd(dirname(rstudioapi::getSourceEditorContext()$path))


local <- read.table("exp_assembly_local_energy_template_1677512126790.txt", 
                    header = F,
                    sep = " ",
                    col.names= c("locx","locy")
)


local_x <- local$locx
local_y <- local$locy

head(local)

colors <- c("local"="red")

ggplot(data=local, aes(x=local_x, col = group)) + 
  
  geom_line(aes(y = local_y, color = "local"), size = 1) +
  
  labs(x = "round",
       y = "Services fully resolved (total/solved)",
       color = "Legend",
       title = "Services fully resolved (total/solved) - Small battery, alpha=0.5, H=50", 
       caption = "Learning cycles=2000, Simulation experiments=50, Network size=50, services per node=5, types=10") +
  
  scale_color_manual(values = colors) +
  ylim(0.85, 1)


